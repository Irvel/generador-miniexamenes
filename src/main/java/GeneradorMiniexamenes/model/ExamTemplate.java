package GeneradorMiniexamenes.model;

import java.util.ArrayList;

/**
 * A template model for generating LaTeX exams given a set of Exams
 */
public class ExamTemplate {
    /**
     * mdocumentHeader
     *
     * Imports the used LaTeX packages and sets the page layout, encoding, headers and removes
     * the extra vertical margin in the first page (vspace*{-1.2cm}).
     */
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

    /**
     * getTitle
     *
     * Generate a LaTeX string representing the main title of the exam to be shown. This title is
     * composed of the name of the course, the name of the subject and the user-entered group.
     *
     * @param course The name of the class course
     * @param subject The name of the subject from the generated exams
     * @param group The group from the generated exams
     * @return The LaTeX string representing the main title of the exam to be shown
     */
    private static String getTitle(String course, String subject, String group) {
        return "   \\begin{center}\n" +
                "      \\scshape \\large Miniexamen " + course + " \\\\ (" +
                subject + ", Grupo " + group + ")\\vspace{-.3em}\n" +
                "   \\end{center}\n" +
                "   \\thispagestyle{empty}\n" +
                "   \\noindent\n";
    }

    /**
     * getFirstSection
     *
     * Generate a LaTeX string representing a first section of the exam. This first section is
     * the part in which the student enters his/her ID and in which the Exam number is displayed.
     *
     * @param examNumber The exam number to be displayed
     * @return The LaTeX string representing the first section of the exam
     */
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

    /**
     * getQuestions
     *
     * Generate a LaTeX string representing a set of questions. This set of questions is given as
     * a group and provides numbering to each question and for its multiple option answers.
     *
     * @param questions A list of questions that will be converted into the LaTeX string
     * @return The LaTeX string representing the set of questions
     */
    private static String getQuestions(ArrayList<Question> questions) {
        // Open a new Questions section
        String sQuestions = "   \\begin{questions}\n";
        for (Question q : questions) {
            sQuestions += "      \\question " + q.getQuestion() + ":\n";
            // Add a box so that the student can fill in the answer
            sQuestions += "      \\framebox(14,14){}\n";
            // Open the multiple options section
            sQuestions += "      \\begin{parts}\n";
            for (Answer a : q.getAnswers()) {
                sQuestions += "         \\part " + a.getAnswer() + "\n";
            }
            sQuestions += "      \\end{parts}\n";
        }
        sQuestions += "   \\end{questions}\n";
        return sQuestions;
    }

    /**
     * makeLatexExam
     *
     * Generate a LaTeX string containing a single exam. This string is not parseable because it
     * lacks the header and closing part, however it can be grouped along other sections of the
     * same kind.
     * @param exam The exam used to to create the LaTeX string
     * @param examNumber The exam number to be displayed in the exam second line
     * @return The LaTeX string with the section containing a single exam
     */
    private static String makeLatexExam(Exam exam, String examNumber) {
        // TODO: Add a way to edit this class name in the user interface
        final String className = "Teoría de la Computación";
        final String title = getTitle(className, exam.getSubject(), exam.getGroup());
        final String questions = getQuestions(exam.getQuestions());
        final String firstSection = getFirstSection(examNumber);
        return title + firstSection + questions;
    }

    /**
     * makeLatexExams
     *
     * Generate a parseable LaTeX string containing a document with a set of exams.
     * @param exams The set of exams from which the document will be created
     * @return The LaTeX string containing the exams
     */
    public static String makeLatexExams(ArrayList<Exam> exams) {
        // Get the document header
        String latexExams = mdocumentHeader;

        // Used to graphically numerate the exams from 1 to N
        int examNumber = 1;
        for (Exam exam : exams) {
            if (examNumber < exams.size()) {
                // Insert a new page after each exam that is not the last one
                latexExams += makeLatexExam(exam, Integer.toString(examNumber)) + "   \\newpage\n";
            }
            else {
                latexExams += makeLatexExam(exam, Integer.toString(examNumber));
            }
            examNumber++;
        }
        // Finish the LaTeX document
        return latexExams + "\\end{document}";
    }

}
