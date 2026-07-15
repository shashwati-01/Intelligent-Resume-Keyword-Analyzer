import java.util.*;
import java.io.*;

// ---------------- ABSTRACT CLASS ----------------
abstract class Document {
    protected String id;
    protected String content;

    public Document(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public abstract void display();
}

// ---------------- RESUME CLASS ----------------
class Resume extends Document {
    String name;

    public Resume(String id, String name, String content) {
        super(id, content);
        this.name = name;
    }

    @Override
    public void display() {
        System.out.println("Resume: " + name + " (" + id + ")");
    }
}

// ---------------- JOB DESCRIPTION CLASS ----------------
class JobDescription extends Document {
    String title;

    public JobDescription(String id, String title, String content) {
        super(id, content);
        this.title = title;
    }

    @Override
    public void display() {
        System.out.println("Job: " + title + " (" + id + ")");
    }
}

// ---------------- INTERFACE ----------------
interface Analyzer {
    Report analyze(Resume r, JobDescription j);
}

// ---------------- REPORT CLASS ----------------
class Report {
    String candidateName, jobTitle;
    double percent;
    List<String> matched;

    public Report(String c, String j, double p, List<String> m) {
        this.candidateName = c;
        this.jobTitle = j;
        this.percent = p;
        this.matched = m;
    }

    public void show() {
        System.out.println("\n----- REPORT -----");
        System.out.println("Candidate: " + candidateName);
        System.out.println("Job: " + jobTitle);
        System.out.println("Match: " + percent + "%");
        System.out.println("Matched words: " + matched);
    } 
}

// ---------------- SIMPLE ANALYZER ----------------
class SimpleAnalyzer implements Analyzer {

    private Set<String> cleanWords(String text) {

        // Split, remove symbols, lowercase
        String[] arr = text.toLowerCase().replaceAll("[^a-z ]", " ").split("\\s+");

        // Java 8 compatible stopwords
        Set<String> stop = new HashSet<>(Arrays.asList(
            "the","and","for","are","was","have","has","with","this",
            "that","you","your","from","into","been","were","but",
            "not","can","will","she","him","her","his","its","our"
        ));

        Set<String> out = new HashSet<>();
        for (String w : arr) {
            if (w.length() > 2 && !stop.contains(w)) {
                out.add(w);
            }
        }
        return out;
    }

    @Override
    public Report analyze(Resume r, JobDescription j) {

        Set<String> jobWords = cleanWords(j.content);
        Set<String> resumeWords = cleanWords(r.content);

        List<String> matched = new ArrayList<>();
        for (String w : jobWords) {
            if (resumeWords.contains(w)) matched.add(w);
        }

        double score = jobWords.isEmpty() ? 0 :
                       (matched.size() * 100.0 / jobWords.size());

        return new Report(r.name, j.title, score, matched);
    }
}
 
// ---------------- MAIN ATS PROJECT ----------------
public class ATSProject {

    static Scanner sc = new Scanner(System.in);
    static Map<String, Resume> resumes = new HashMap<>();
    static Map<String, JobDescription> jobs = new HashMap<>();
    static Analyzer analyzer = new SimpleAnalyzer();

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== ATS MENU =====");
            System.out.println("1. Add Job");
            System.out.println("2. Add Resume");
            System.out.println("3. Analyze Resume");
            System.out.println("4. Exit");
            System.out.print("Enter: ");

            int ch = Integer.parseInt(sc.nextLine());

            switch (ch) {
                case 1: addJob(); break;
                case 2: addResume(); break;
                case 3: analyze(); break;
                case 4: System.exit(0); break;
                default: System.out.println("Invalid!");
            }
        }
    }

    static void addJob() {
        try {
            System.out.print("Job ID: ");
            String id = sc.nextLine();
            System.out.print("Job Title: ");
            String title = sc.nextLine();
            System.out.print("Description: ");
            String content = sc.nextLine();

            JobDescription j = new JobDescription(id, title, content);
            jobs.put(id, j);

            saveToFile("job_" + id + ".txt", content);
            System.out.println("Job saved!");

        } catch (Exception e) {
            System.out.println("Error adding job.");
        }
    }

    static void addResume() {
        try {
            System.out.print("Resume ID: ");
            String id = sc.nextLine();
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Content: ");
            String content = sc.nextLine();

            Resume r = new Resume(id, name, content);
            resumes.put(id, r);

            saveToFile("resume_" + id + ".txt", content);
            System.out.println("Resume saved!");

        } catch (Exception e) {
            System.out.println("Error adding resume.");
        }
    }

    static void analyze() {
        System.out.print("Job ID: ");
        String jid = sc.nextLine();

        if (!jobs.containsKey(jid)) {
            System.out.println("Job not found!");
            return;
        }

        System.out.print("Resume ID: ");
        String rid = sc.nextLine();

        if (!resumes.containsKey(rid)) {
            System.out.println("Resume not found!");
            return;
        }

        Report rpt = analyzer.analyze(resumes.get(rid), jobs.get(jid));
        rpt.show();
    }

    static void saveToFile(String filename, String data) {
        try (PrintWriter pw = new PrintWriter(filename)) {
            pw.println(data);
        } catch (Exception e) {
            System.out.println("File error.");
        }
    }
}