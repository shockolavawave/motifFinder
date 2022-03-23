import javax.swing.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Scanner;

// class used to create file objects
class oneFile {

    // input variables/objects
    private File myFile;
    public File getMyFile() {   return myFile;    }
    public void setMyFile(File myFile) {     this.myFile = myFile;    }

    private String headerLine;
    public String getHeaderLine() {     return headerLine;    }
    public void setHeaderLine(String headerLine) {  this.headerLine = headerLine;    }

    private String rawSeq;
    public String getRawSeq() {     return rawSeq;    }
    public void setRawSeq(String rawSeq) {    this.rawSeq = rawSeq;    }

    private String sequence;
    public String getSequence() {    return sequence;    }
    public void setSequence(String sequence) {    this.sequence = sequence;    }

    // derived variables/objects
    private String summary;
    public String getSummary() {    return summary;    }
    public void setSummary(String summary) {   this.summary = summary;    }

    private int length;
    public int getLength() {   return length;    }
    public void setLength(int length) {    this.length = length;    }
}

// class created for secondary result window
class finMo extends JFrame{

    // the lone component
    JTextArea inTxtA = new JTextArea();
    JScrollPane in_jsp = new JScrollPane(inTxtA,
                                                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // no parameter constructor if either of the thing goes missing
    finMo() {

        JLabel la = new JLabel("Something is missing");
        la.setBounds(10, 10, 150, 30);

        add(la);

        setSize(147, 81);
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    // double parameter constructor
    finMo(oneFile m, String motif) {

        // aligning the textField
        in_jsp.setBounds(10, 10, 470, 450);
        inTxtA.setEditable(false);

        /* --- from here you can start coding, shreya

               you can get the  sequence by saying m.getSequence()
               and the motif is directly available
               also, you can call any other function of m.
               do refer the code I sent you in c
               in the end just include everything in the inTxtA.setText(*whatever the answer may ne*);
        *  --- */

        // adding panel
        add(in_jsp);

        // setting the frame
        setSize(500, 500);
        setLayout(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}

// main frame
class win extends JFrame{

    // GUI components
        // buttons:
    JButton buLoad = new JButton("load");
    JButton buSumm = new JButton("get summary");
    JButton buclr  = new JButton("clear");
    JButton buFind  = new JButton("Find");
        // labels
    JLabel la1 = new JLabel();
    JLabel la2 = new JLabel("Enter motif:");
        // texts
    JTextArea txtA = new JTextArea("*kochchi wo miro*");
    JTextField txtF = new JTextField();
    JScrollPane jsp = new JScrollPane(txtA,
                                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // operational objects
    Scanner scObj;
    oneFile m = new oneFile();

    // constructor
    win() {

        // setting up text area
        jsp.setBounds(25, 120, 935, 325);
        txtA.setEditable(false);

        // setting labels and text field
        la1.setBounds(150, 25, 250, 30);
        la2.setBounds(730, 25, 60, 30 );
        txtF.setBounds(810, 25, 150, 30);

        // setting buttons and respective action listeners
        buLoad.setBounds(25, 25, 100, 30);
        buLoad.addActionListener(e_load -> {
            try {

                // getting the file
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("FASTA ONLY");

                StringBuilder text = new StringBuilder();

                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                    m.setMyFile(chooser.getSelectedFile());

                    if (!formatCheck(m.getMyFile()))
                        throw new Exception("only FASTA files are allowed.");
                }

                la1.setText(m.getMyFile().getName());

                // storing header and raw sequence
                scObj = new Scanner(m.getMyFile());
                try {
                    for (int i = 0; scObj.hasNext();) {

                        if (i == 0) {
                            m.setHeaderLine(scObj.nextLine());
                            i++;
                        } else
                            text.append(scObj.nextLine())
                                .append('\n');
                    }

                    m.setRawSeq(text.toString());

                } catch (NullPointerException ne) {
                    txtA.setText("something went wrong" + ne.getMessage());
                }

                txtA.setText(m.getHeaderLine() + '\n' + m.getRawSeq());

            } catch (Exception e1) {
                txtA.setText("something went wrong: " + e1.getMessage());
            }
        }); // end of event performed by buLoad

        buSumm.setBounds(25, 70, 100, 30);
        buSumm.addActionListener(e_summ -> {

            try {
                // extracting tha actual sequence
                StringBuilder buff = new StringBuilder();

                for (int i = 0; i < m.getRawSeq().length(); i++) {

                    if (m.getRawSeq().charAt(i) == '\n')
                        continue;

                    buff.append(m.getRawSeq().charAt(i));
                }

                m.setSequence(buff.toString().toUpperCase());
                m.setLength(m.getSequence().length());

                String seqType = getSequenceType(m.getSequence());

                // switching according to the sequence type
                switch (seqType) {

                    case "DNA" -> {
                        int a_count = 0, g_count = 0, t_count = 0, c_count = 0;

                        for (int i = 0; i < m.getLength(); i++) {
                            switch (m.getSequence().charAt(i)) {
                                case 'A' -> a_count++;
                                case 'G' -> g_count++;
                                case 'T' -> t_count++;
                                case 'C' -> c_count++;
                            }
                        }

                        double a_perc = (a_count / (float) m.getLength()) * 100;
                        double g_perc = (g_count / (float) m.getLength()) * 100;
                        double t_perc = (t_count / (float) m.getLength()) * 100;
                        double c_perc = (c_count / (float) m.getLength()) * 100;

                        // setting up formats for display
                        DecimalFormat dF1 = new DecimalFormat("0000");
                        DecimalFormat dF2 = new DecimalFormat("000.####");

                        m.setSummary(
                               "Type of sequence: " + seqType + "\n\n" +

                               "Adenine  (A) : " + dF1.format(a_count) + " residues         " + dF2.format(a_perc) + " %" + '\n' +
                               "Guanine  (G) : " + dF1.format(g_count) + " residues         " + dF2.format(g_perc) + " %" + '\n' +
                               "Thymine  (T) : " + dF1.format(t_count) + " residues         " + dF2.format(t_perc) + " %" + '\n' +
                               "Cytosine (C) : " + dF1.format(c_count) + " residues         " + dF2.format(c_perc) + " %" + "\n\n" +

                               "Sequence length: " + m.getLength() + "\n\n" +

                               "Your sequence is:\n\n" + seqDisplay(m.getSequence()) + "\n\n"
                        );

                        txtA.setText(m.getSummary());

                    }// end of case DNA

                    case "RNA" -> {
                        int a_count = 0, g_count = 0, u_count = 0, c_count = 0;

                        for (int i = 0; i < m.getLength(); i++) {
                            switch (m.getSequence().charAt(i)) {
                                case 'A' -> a_count++;
                                case 'G' -> g_count++;
                                case 'U' -> u_count++;
                                case 'C' -> c_count++;
                            }
                        }

                        double a_perc = (a_count / (float) m.getLength()) * 100;
                        double g_perc = (g_count / (float) m.getLength()) * 100;
                        double u_perc = (u_count / (float) m.getLength()) * 100;
                        double c_perc = (c_count / (float) m.getLength()) * 100;

                        DecimalFormat dF1 = new DecimalFormat("0000");
                        DecimalFormat dF2 = new DecimalFormat("000.####");

                        m.setSummary(
                               "Type of sequence: " + seqType + "\n\n" +

                               "Adenine  (A) : " + dF1.format(a_count) + " residues         " + dF2.format(a_perc) + " %" + '\n' +
                               "Guanine  (G) : " + dF1.format(g_count) + " residues         " + dF2.format(g_perc) + " %" + '\n' +
                               "Uracil   (U) : " + dF1.format(u_count) + " residues         " + dF2.format(u_perc) + " %" + '\n' +
                               "Cytosine (C) : " + dF1.format(c_count) + " residues         " + dF2.format(c_perc) + " %" + "\n\n" +

                               "Sequence length: " + m.getLength() + "\n\n" +

                               "Your sequence is:\n\n" + seqDisplay(m.getSequence()) + "\n\n"
                        );

                        txtA.setText(m.getSummary());

                    }// end of case DNA

                    case "protein" -> {

                        int a_count = 0, c_count = 0, d_count = 0, e_count = 0, f_count = 0,
                            g_count = 0, h_count = 0, i_count = 0, k_count = 0, l_count = 0,
                            m_count = 0, n_count = 0, o_count = 0, p_count = 0, q_count = 0,
                            r_count = 0, s_count = 0, t_count = 0, u_count = 0, v_count = 0,
                            w_count = 0, y_count = 0;

                        for (int i = 0; i < m.getSequence().length(); i++) {
                            switch (m.getSequence().charAt(i)) {
                                case 'A' -> a_count++;
                                case 'C' -> c_count++;
                                case 'D' -> d_count++;
                                case 'E' -> e_count++;
                                case 'F' -> f_count++;
                                case 'G' -> g_count++;
                                case 'H' -> h_count++;
                                case 'I' -> i_count++;
                                case 'K' -> k_count++;
                                case 'L' -> l_count++;
                                case 'M' -> m_count++;
                                case 'N' -> n_count++;
                                case 'O' -> o_count++;
                                case 'P' -> p_count++;
                                case 'Q' -> q_count++;
                                case 'R' -> r_count++;
                                case 'S' -> s_count++;
                                case 'T' -> t_count++;
                                case 'U' -> u_count++;
                                case 'V' -> v_count++;
                                case 'W' -> w_count++;
                                case 'Y' -> y_count++;
                            }
                        }

                        double a_perc = (a_count / (float) m.getLength()) * 100;
                        double c_perc = (c_count / (float) m.getLength()) * 100;
                        double d_perc = (d_count / (float) m.getLength()) * 100;
                        double e_perc = (e_count / (float) m.getLength()) * 100;
                        double f_perc = (f_count / (float) m.getLength()) * 100;
                        double g_perc = (g_count / (float) m.getLength()) * 100;
                        double h_perc = (h_count / (float) m.getLength()) * 100;
                        double i_perc = (i_count / (float) m.getLength()) * 100;
                        double k_perc = (k_count / (float) m.getLength()) * 100;
                        double l_perc = (l_count / (float) m.getLength()) * 100;
                        double m_perc = (m_count / (float) m.getLength()) * 100;
                        double n_perc = (n_count / (float) m.getLength()) * 100;
                        double o_perc = (o_count / (float) m.getLength()) * 100;
                        double p_perc = (p_count / (float) m.getLength()) * 100;
                        double q_perc = (q_count / (float) m.getLength()) * 100;
                        double r_perc = (r_count / (float) m.getLength()) * 100;
                        double s_perc = (s_count / (float) m.getLength()) * 100;
                        double t_perc = (t_count / (float) m.getLength()) * 100;
                        double u_perc = (u_count / (float) m.getLength()) * 100;
                        double v_perc = (v_count / (float) m.getLength()) * 100;
                        double w_perc = (w_count / (float) m.getLength()) * 100;
                        double y_perc = (y_count / (float) m.getLength()) * 100;

                        DecimalFormat dF1 = new DecimalFormat("0000");
                        DecimalFormat dF2 = new DecimalFormat("000.####");

                        m.setSummary(
                               "Type of sequence: " + seqType + "\n\n" +

                               "Alanine         (A) : " + dF1.format(a_count) + " residues         " + dF2.format(a_perc) + " %" + '\n' +
                               "Cystine         (C) : " + dF1.format(c_count) + " residues         " + dF2.format(c_perc) + " %" + '\n' +
                               "Aspartic acid   (D) : " + dF1.format(d_count) + " residues         " + dF2.format(d_perc) + " %" + '\n' +
                               "Glutamic acid   (E) : " + dF1.format(e_count) + " residues         " + dF2.format(e_perc) + " %" + '\n' +
                               "Phenylalanine   (F) : " + dF1.format(f_count) + " residues         " + dF2.format(f_perc) + " %" + '\n' +
                               "Glycine         (G) : " + dF1.format(g_count) + " residues         " + dF2.format(g_perc) + " %" + '\n' +
                               "Histamine       (H) : " + dF1.format(h_count) + " residues         " + dF2.format(h_perc) + " %" + '\n' +
                               "Isoleucine      (I) : " + dF1.format(i_count) + " residues         " + dF2.format(i_perc) + " %" + '\n' +
                               "Lysine          (K) : " + dF1.format(k_count) + " residues         " + dF2.format(k_perc) + " %" + '\n' +
                               "Leucine         (L) : " + dF1.format(l_count) + " residues         " + dF2.format(l_perc) + " %" + '\n' +
                               "Methionine      (M) : " + dF1.format(m_count) + " residues         " + dF2.format(m_perc) + " %" + '\n' +
                               "Asparagine      (N) : " + dF1.format(n_count) + " residues         " + dF2.format(n_perc) + " %" + '\n' +
                               "Pyrrolysine     (O) : " + dF1.format(o_count) + " residues         " + dF2.format(o_perc) + " %" + '\n' +
                               "Proline         (P) : " + dF1.format(p_count) + " residues         " + dF2.format(p_perc) + " %" + '\n' +
                               "Glutamine       (Q) : " + dF1.format(q_count) + " residues         " + dF2.format(q_perc) + " %" + '\n' +
                               "Arginine        (R) : " + dF1.format(r_count) + " residues         " + dF2.format(r_perc) + " %" + '\n' +
                               "Serine          (S) : " + dF1.format(s_count) + " residues         " + dF2.format(s_perc) + " %" + '\n' +
                               "Threonine       (T) : " + dF1.format(t_count) + " residues         " + dF2.format(t_perc) + " %" + '\n' +
                               "Selenocysteine  (U) : " + dF1.format(u_count) + " residues         " + dF2.format(u_perc) + " %" + '\n' +
                               "Valine          (V) : " + dF1.format(v_count) + " residues         " + dF2.format(v_perc) + " %" + '\n' +
                               "Tryptophan      (W) : " + dF1.format(w_count) + " residues         " + dF2.format(w_perc) + " %" + '\n' +
                               "Tyrosine        (Y) : " + dF1.format(y_count) + " residues         " + dF2.format(y_perc) + " %" + "\n\n" +

                               "Sequence length: " + m.getLength() + "\n\n" +

                               "Your sequence is:\n\n" + seqDisplay(m.getSequence()) + "\n\n"
                        );

                        if (o_count != 0 && u_count == 0)
                            txtA.setText(m.getSummary() + "WARNING: Pyrrolysine is present in the sequence.");
                        if (o_count == 0 && u_count != 0)
                            txtA.setText(m.getSummary() + "WARNING: Selenocysteine is present in the sequence.");
                        if (o_count != 0 && u_count != 0)
                            txtA.setText(m.getSummary() + "WARNING: Selenocysteine and Pyrrolysine are present in the sequence.");
                        else
                            txtA.setText(m.getSummary());
                    } // end of case protein

                    case "none" -> txtA.setText("cannot identify the type of sequence");

                }// end of switch
            } catch (Exception exe) {
                txtA.setText("file not loaded yet");
            }

        });// end of action performed

        buclr.setBounds(150, 70, 100, 30);
        buclr.addActionListener(e_clear -> {
            try {
                txtA.setText("*kochchi wo miro*");
                txtF.setText("");
            } catch (Exception e) {
                // if file not loaded, it will throw a null pointer exception
                txtA.setText("file not loaded yet");
            }
        });

        buFind.setBounds(860, 70, 100, 30);
        buFind.addActionListener(e_find -> {
            try {

                // if motif textField is empty || file is not loaded
                if (txtF.getText().equals("") || m.getMyFile() == null)
                    new finMo();
                // if both the requirements are fulfilled
                else
                    new finMo(m, txtF.getText());

            } catch (Exception e) {
                txtA.setText("something went wrong");
            }
        });
        
        // adding elements
        add(buLoad); add(buSumm); add(buclr); add(buFind);  // buttons
        add(la1); add(la2);                                 // labels
        add(jsp); add(txtF);                                // texts

        // setting up the JFrame
        setName("MoFin");
        setLayout(null);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 500);
    }

    // for aesthetic look of the sequence
    private String seqDisplay(String sequence) {
        StringBuilder fin = new StringBuilder();

        for (int i = 0; i < sequence.length(); i++){

            if(i%10 == 0 && i != 0)
                fin.append('|');

            if(i%60 == 0 && i != 0)
                fin.append('\n');

            fin.append(sequence.charAt(i));
        }
        return fin.toString();
    }

    // for checking of file format
    public static boolean formatCheck(File fl) {

        String name = fl.getName();
        StringBuilder buff = new StringBuilder();

        for (int i = (name.length() - 1); i != 0 ; i--) {

            if (name.charAt(i) == '.')
                break;

            buff.append(name.charAt(i));
        }

        String format = buff.reverse().toString().toLowerCase();

        return (format.equals("fasta") || format.equals("fna") ||
                format.equals("ffn") || format.equals("faa") ||
                format.equals("frn") || format.equals("fa"));
    }

    // tells the sequence type
    private String getSequenceType(String sequence) {

        if (isDNA(sequence))
            return "DNA";
        else if (isRNA(sequence))
            return "RNA";
        else if (isProtein(sequence))
            return "protein";
        else
            return "none";
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private boolean isDNA(String sequence) {

        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == 'A' || sequence.charAt(i) == 'G' || sequence.charAt(i) == 'T' || sequence.charAt(i) == 'C'){
                // null block
            }
            else
                return false;
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private boolean isRNA(String sequence) {

        for (int i = 0; i < sequence.length(); i++) {
            if (sequence.charAt(i) == 'A' || sequence.charAt(i) == 'G' || sequence.charAt(i) == 'U' || sequence.charAt(i) == 'C'){
                // null block
            }
            else
                return false;
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private boolean isProtein(String sequence) {

        for (int i = 0; i < sequence.length(); i++) {
            if (
                   sequence.charAt(i) == 'A' || sequence.charAt(i) == 'C' || sequence.charAt(i) == 'D' || sequence.charAt(i) == 'E' ||
                   sequence.charAt(i) == 'F' || sequence.charAt(i) == 'G' || sequence.charAt(i) == 'H' || sequence.charAt(i) == 'I' ||
                   sequence.charAt(i) == 'K' || sequence.charAt(i) == 'L' || sequence.charAt(i) == 'M' || sequence.charAt(i) == 'N' ||
                   sequence.charAt(i) == 'O' || sequence.charAt(i) == 'P' || sequence.charAt(i) == 'Q' || sequence.charAt(i) == 'R' ||
                   sequence.charAt(i) == 'S' || sequence.charAt(i) == 'T' || sequence.charAt(i) == 'U' || sequence.charAt(i) == 'V' ||
                   sequence.charAt(i) == 'W' || sequence.charAt(i) == 'Y'
            ){
                // null block
            }
            else
                return false;
        }

        return true;
    }
}

public class mianClass{
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new win();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
}
