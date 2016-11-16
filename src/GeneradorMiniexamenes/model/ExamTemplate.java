package GeneradorMiniexamenes.model;

import java.util.ArrayList;

/**
 * Created by Irvel on 11/13/16.
 */
public class ExamTemplate {
    private static final String mdocumentHeader =
            "\\documentclass[fleqn]{exam}\n" +
            "\\usepackage[letter, left=.5in, right=.5in, top=.25in, " +
            "bottom=.5in]{geometry}\n" +
            "\\usepackage{mathexam}\n" +
            "\\usepackage[utf8]{inputenc}\n" +
            "\\usepackage{amsmath}\n" +
            "\\usepackage{fancyhdr}\n" +
            "\\fancyhf{}\n" +
            "\\begin{document}\n" +
            "   \\vspace*{-1.2cm}\n";

    private static String getTitle(String course, String subject, String group) {
        return "   \\begin{center}\n" +
                "      \\scshape \\large Miniexamen " + course + " \\\\ (" +
                subject + ", Grupo " + group + ")\\vspace{-.3em}\n" +
                "   \\end{center}\n" +
                "   \\thispagestyle{empty}\n" +
                "   \\noindent\n";
    }

    private static String getFirstSection(String examNumber) {
        return  "   \\text Número de Exámen: " + examNumber +
                " \\hspace{75 mm}\n" +
                "   \\text Matrícula:\n" +
                "   \\makebox[1in]{\\hrulefill}\n" +
                "   \\text (No escribir nombre)\\\\\n" +
                "   \\textbf{\\underline{NOTA:}}\n" +
                "   \\text En las preguntas de opción múltiple, escribe en el cuadro la letra que" +
                " corresponda a la opción seleccionada.\n";
    }

    private static String getQuestions(ArrayList<Question> questions) {
        String sQuestions = "   \\begin{questions}\n";
        for (Question q : questions) {
            sQuestions += "      \\question " + q.getQuestion() + ":\n";
            sQuestions += "      \\framebox(14,14){}\n";
            sQuestions += "      \\begin{parts}\n";
            for (Answer a : q.getAnswers()) {
                sQuestions += "         \\part " + a.getAnswer() + "\n";
            }
            sQuestions += "      \\end{parts}\n";
        }
        sQuestions += "   \\end{questions}\n";
        return sQuestions;
    }

    public static String makeLatexExam(Exam exam, String examNumber) {
        String title = getTitle("Teoría de la Computación", exam.getSubject(), exam.getGroup());
        // TODO: Add an exam number somewhere and remove this hardcoded thing
        String questions = getQuestions(exam.getQuestions());
        String firstSection = getFirstSection(examNumber);
        return title + firstSection + questions;
    }

    public static String makeLatexExams(ArrayList<Exam> exams) {
        String latexExams = mdocumentHeader;
        int examNumber = 1;
        for (Exam exam : exams) {
            if (examNumber < exams.size()) {
                latexExams += makeLatexExam(exam, Integer.toString(examNumber)) + "   \\newpage\n";
            }
            else {
                latexExams += makeLatexExam(exam, Integer.toString(examNumber));
            }
            examNumber++;
        }
        return latexExams + "\\end{document}";
    }

}
