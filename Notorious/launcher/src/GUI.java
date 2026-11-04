import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;

class GUI
extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int percent = 0;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JProgressBar jProgressBar1;

    public GUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception var1_1) {
            // empty catch block
        }
        this.initComponents();
    }

    private void initComponents() {
        this.jProgressBar1 = new JProgressBar();
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.setDefaultCloseOperation(2);
        this.setTitle(Loader.SERVER_NAME + "Client Update");
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent evt) {
                GUI.this.formWindowClosing(evt);
            }
        });
        this.jLabel1.setText("Status:");
        this.jLabel2.setText("Downloading " + Loader.SERVER_NAME + " Client...");
        this.jLabel3.setText("0%");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 304, 32767).addComponent(this.jLabel3)).addComponent(this.jProgressBar1, GroupLayout.Alignment.TRAILING, -1, 380, 32767)).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel2, -1, -1, 32767).addComponent(this.jLabel3)).addComponent(this.jLabel1, -1, -1, 32767)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jProgressBar1, -2, 30, -2).addContainerGap(-1, 32767)));
        this.pack();
    }

    private void formWindowClosing(WindowEvent evt) {
    }

    public void setStatus(String s) {
        this.jLabel2.setText(s);
    }

    public String getStatus() {
        return this.jLabel2.getText();
    }

    public void setPercent(int amount) {
        this.percent = amount;
        this.jLabel3.setText(String.valueOf(amount) + "%");
        this.jProgressBar1.setValue(amount);
    }

    public int getPercent() {
        return this.percent;
    }

}

