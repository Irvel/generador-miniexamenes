package GeneradorMiniexamenes.model;

import java.util.ArrayList;

/**
 * A template model for generating LaTeX exams given a set of Exams
 *
 * Important considerations for a nice exam:
 *      - When entering a formula, Enclose it with $$ for example:
 *         In physics, the mass-energy equivalence is stated by the equation $E=mc^2$
 *
 *      - Escape any special of the 10 special LaTeX characters: & % $ # _ { } ~ ^ \ with a backslash
 *        (\) before them.
 *
 */
public class ExamTemplate {
    /**
     * mDocumentHeader
     *
     * Imports the used LaTeX packages and sets the page layout, encoding, headers and removes
     * the extra vertical margin in the first page (vspace*{-1.2cm}).
     * NOTE: User entered formulas must be inside $$ to be properly displayed
     */
    private static final String mDocumentHeader =
            "\\documentclass[fleqn]{exam}\n" +
            "\\usepackage[letterpaper, left=.5in, right=.5in, top=.25in, " +
            "bottom=.5in]{geometry}\n" +
            "\\usepackage[nohdr]{mathexam}\n" +
            "\\usepackage[utf8]{inputenc}\n" +
            "\\usepackage{amsmath}\n" +
            "\\begin{document}\n" +
            "   \\vspace*{-1.2cm}\n";

    /**
     * getTitle
     *
     * GenerateExamsController a LaTeX string representing the main title of the exam to be shown. This title is
     * composed of the name of the course, the name of the subject and the user-entered group.
     *
     * @param course The name of the class course
     * @param subject The name of the subject from the generated exams
     * @param group The group from the generated exams
     * @return The LaTeX string representing the main title of the exam to be shown
     */
    private static String getTitle(String course, String subject, String group) {
        return "   \\begin{center}\n" +
                "      \\scshape \\large " + course + " \\\\ (" +
                subject + ", Grupo " + group + ")\\vspace{-.3em}\n" +
                "   \\end{center}\n" +
                "   \\thispagestyle{empty}\n" +
                "   \\noindent\n";
    }

    /**
     * getFirstSection
     *
     * GenerateExamsController a LaTeX string representing a first section of the exam. This first section is
     * the part in which the student enters his/her ID and in which the Exam number is displayed.
     *
     * @param examNumber The exam number to be displayed
     * @return The LaTeX string representing the first section of the exam
     */
    private static String getFirstSection(String examNumber) {
        return  "   \\text Número de Examen: " + examNumber +
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
     * GenerateExamsController a LaTeX string representing a set of questions. This set of questions is given as
     * a group and provides numbering to each question and for its multiple option answers.
     *
     * @param questions A list of questions that will be converted into the LaTeX string
     * @return The LaTeX string representing the set of questions
     */
    private static String getQuestions(ArrayList<Question> questions) {
        // Open a new Questions section
        String sQuestions = "   \\begin{questions}\n";
        for (Question q : questions) {
            // Add the question header
            sQuestions += "      \\question ";
            // Add a box so that the student can fill in the answer
            sQuestions += " \\framebox(14,14){} ";
            // Add the actual question text
            sQuestions +=  q.getQuestion() + ":\n";
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
     * GenerateExamsController a LaTeX string containing a single exam. This string is not parseable because it
     * lacks the header and closing part, however it can be grouped along other sections of the
     * same kind.
     * @param exam The exam used to to create the LaTeX string
     * @param examNumber The exam number to be displayed in the exam second line
     * @return The LaTeX string with the section containing a single exam
     */
    private static String makeLatexExam(String headerText, Exam exam, String examNumber) {
        final String title = getTitle(headerText, exam.getSubject(), exam.getGroup());
        final String questions = getQuestions(exam.getQuestions());
        final String firstSection = getFirstSection(examNumber);
        return title + firstSection + questions;
    }

    /**
     * makeLatexExams
     *
     * GenerateExamsController a parseable LaTeX string containing a document with a set of exams.
     * @param exams The set of exams from which the document will be created
     * @return The LaTeX string containing the exams
     */
    public static String makeLatexExams(String headerText, ArrayList<Exam> exams) {
        // Get the document header
        String latexExams = mDocumentHeader;

        int examCount = 0;
        for (Exam exam : exams) {
            if (examCount < exams.size()) {
                // Insert a new page after each exam that is not the last one
                latexExams += makeLatexExam(headerText,
                                            exam,
                                            Integer.toString(exam.getExamNumber())) + "   \\newpage\n";
            }
            else {
                latexExams += makeLatexExam(headerText,
                                            exam,
                                            Integer.toString(exam.getExamNumber()));
            }
            examCount++;
        }
        // Finish the LaTeX document
        return latexExams + "\\end{document}";
    }

}
