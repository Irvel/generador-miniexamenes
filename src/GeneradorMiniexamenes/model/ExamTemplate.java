package GeneradorMiniexamenes.model;

import java.util.ArrayList;

/**
 * Created by Irvel on 11/13/16.
 */
public class ExamTemplate {
    private static final String mHeader =
            "\\documentclass[fleqn]{exam}\n" +
            "\\usepackage[left=.5in, right=.5in, top=.2in, bottom=.5in]{geometry}\n" +
            "\\usepackage{mathexam}\n" +
            "\\usepackage[utf8]{inputenc}\n" +
            "\\usepackage{amsmath}\n" +
            "\\usepackage{fancyhdr}\n" +
            "\\fancyhf{}\n";

    private static String getTitle(String course, String subject, String group) {
        return "\\ExamClass{Miniexamen " +
                course +
                " \\\\ (" +
                subject +
                ", Grupo " +
                group +
                ")}\n\\let\\ds\\displaystyle\n";
    }

    private static String getFirstSection(String examNumber) {
        return  "\\begin{document}\n" +
                "\\text Número de Examen: " + examNumber +
                "\\hspace{75 mm}\n" +
                "\\text Matrícula:\n" +
                "\\makebox[1in]{\\hrulefill}\n" +
                "\\text (No escribir nombre)\\\\\n" +
                "\\textbf{\\underline{NOTA:}}\n" +
                "\\text En las preguntas de opción múltiple, escribe en el cuadro la letra que " +
                "corresponda a la opción seleccionada.\n";
    }

    private static String getQuestions(ArrayList<Question> questions) {
        String sQuestions = "\\begin{questions}\n";
        for (Question q : questions) {
            sQuestions += "\\question " + q.getQuestion() + ":";
            sQuestions += "\\framebox(14,14){}\n";
            sQuestions += "\\begin{parts}";
            for (Answer a : q.getAnswers()) {
                sQuestions += "\\part " + a.getAnswer() + "\n";
            }
            sQuestions += "\\end{parts}";
        }
        sQuestions += "\\end{questions}\n";
        return sQuestions;
    }

    public static String makeLatexExam(Exam exam) {
        String title = getTitle("Teoría de la Computación", exam.getSubject(), exam.getGroup());
        // TODO: Add an exam number somewhere and remove this hardcoded thing
        String questions = getQuestions(exam.getQuestions());
        return title + questions + "\n";
    }

    public static String makeLatexExams(ArrayList<Exam> exams) {
        String latexExams = mHeader;
        String firstSection = getFirstSection("1");
        for (Exam exam : exams) {
            latexExams += makeLatexExam(exam);
        }
        return latexExams + "\\end{document}";
    }

}
