package eurex.clear;

/**
 * Created by carvcal on 28.01.2015.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public class CheckQs implements ActionListener{

    public static ArrayList<String> Qnames = new ArrayList<String>();

    int c=0;

    int depth = 0;

    String queueManagerName;

    String queueName;

    String server, CCSID; int port; String channel;

    MQQueue destQueue,outputQueue;

    MQQueueManager queueManager;

    int openOptions=0;

    static ArrayList<String> loadedQs = new ArrayList<String>();

    static ArrayList<Integer> loadedDepth = new ArrayList<Integer>();

    String newQ[];

//Vector data;

    private String env;

    int counterQ=0;

    JButton stop;

    private JTextField txt;

    public JLabel statusbar;

    private  JButton clear;



    Boolean runMode=true;

    JCheckBox chk;

    JCheckBox saveMsg;

    String state="Empty";

    boolean saveNosaveMsg;





    public CheckQs(){



    }



    public class jtableLoad extends JFrame{



        private static final long serialVersionUID = 1L;

        private  JPanel                   topPanel;

        private  JTable                   table;

        private  JScrollPane scrollPane;

        private  JToolBar toolbar;

        private JToolBar secondBar;

        private JToolBar serverBar;

        private JButton refresh;

        private JComboBox selectEnvCombo;

        private JTextField serverN;

        private JTextField portN;

        private JTextField channelN;

        private JTextField qmN;



        public jtableLoad() throws Exception

        {



            toolbar = new JToolBar("Still draggable");

            secondBar= new JToolBar("Still draggable");

            serverBar= new JToolBar("Still draggable");

            refresh = new JButton("Auto Refresh");

            stop = new JButton("Stop Auto Refresh");

            clear = new JButton("Clear Q");

            txt = new JTextField();

            chk= new JCheckBox("Continue Clearing");

            saveMsg= new JCheckBox("Save Clearing Mesgs History");



            stop.setEnabled(false);

            clear.setEnabled(false);



            txt.setBackground(Color.white);

            txt.setForeground(Color.blue);

            txt.setEditable(false);

            setTitle( "View Messages in The Q" );

            setSize( 300, 200 );

            setBackground( Color.blue );





            // Create a panel to hold all other components

            topPanel = new JPanel();

            topPanel.setLayout( new BorderLayout() );

            getContentPane().add( topPanel );



            // Create the custom data model

            final CustomDataModel customDataModel = new CustomDataModel();

            //final CustomDataModel customDataModel = new CustomDataModel("C:\\Data\\Qnames\\AllQs.txt");

            customDataModel.RunAllCode("C:\\Data\\Qnames\\AllQs.txt");

            // Create a new table instance

            table = new JTable( customDataModel ); //dataValues, columnNames );

            table.getColumnModel().getColumn(1).setCellRenderer(new CellColor());

            table.getColumnModel().getColumn(0).setCellRenderer(new CellColor());

            //CreateColumns();



            // Configure some of JTable's paramters

            table.setShowHorizontalLines( true );

            table.setRowSelectionAllowed( true );

            table.setColumnSelectionAllowed( true );



            // Change the selection colour

            table.setSelectionForeground( Color.white );

            table.setSelectionBackground( Color.blue);



            table.setBackground(Color.white);

            table.setForeground(Color.BLUE);

            toolbar.add(refresh);

            toolbar.add(stop);

            toolbar.addSeparator();

            toolbar.add(txt);

            toolbar.addSeparator();

            toolbar.add(chk);

            toolbar.addSeparator();

            toolbar.add(clear);

            JMenuBar menu = new JMenuBar();

            JMenu file = new JMenu("File");



            JMenu view = new JMenu("View");

            view.setMnemonic(KeyEvent.VK_V);

            JCheckBoxMenuItem sbar = new JCheckBoxMenuItem("Show MQ Connection");



            sbar.setState(false);

            view.add(sbar);



            sbar.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {

                    if (serverBar.isVisible()) {

                        serverBar.setVisible(false);





                    } else {

                        serverBar.setVisible(true);

                        customDataModel.setMQServerDetails(serverN.getText(),portN.getText(),channelN.getText(),qmN.getText());

                    }

                }



            });



            JMenuItem exit = new JMenuItem("Exit");

            exit.setMnemonic(KeyEvent.VK_C);

            exit.setToolTipText("Exit application");

            exit.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {



                    System.exit(0);

                }



            });



            file.add(view);

            file.add(exit);

            menu.add(file);



            toolbar.add(menu);

            setJMenuBar(menu);

            statusbar = new JLabel("Q Monitor");

            statusbar.setForeground(Color.blue);

            statusbar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

            add(statusbar, BorderLayout.SOUTH);



            SelectionListener listener = new SelectionListener(table);

            table.getSelectionModel().addListSelectionListener(listener);

            table.getColumnModel().getSelectionModel()

                    .addListSelectionListener(listener);



            chk.addItemListener(new ItemListener(){

                public void itemStateChanged(ItemEvent e) {

                    if (e.getStateChange() == ItemEvent.SELECTED)

                    {

                        System.out.println("TICKED");

                        state="TICKED";

                    }

                    else

                    {

                        System.out.println("UNTICKED");

                        state="UNTICKED";

                    }

                }

            });

            saveMsg.addItemListener(new ItemListener(){

                public void itemStateChanged(ItemEvent e) {

                    if (e.getStateChange() == ItemEvent.SELECTED)

                    {

                        System.out.println("saveMsg TICKED");

                        saveNosaveMsg=true;



                    }

                    else

                    {

                        System.out.println("saveMsg UNTICKED");

                        saveNosaveMsg=false;



                    }



                }

            });

            refresh.addActionListener(new ActionListener(){



                public void actionPerformed(ActionEvent e){

                    Thread con = new Thread(new Runnable(){

                        public void run()

                        {

                            runMode=true;

                            refresh.setEnabled(false);

                            stop.setEnabled(true);

                            while(runMode){

                                System.out.println("running Thread" );

                                try {

                                    Thread.sleep(2000);

                                    customDataModel.setMQServerDetails(serverN.getText(),portN.getText(),channelN.getText(),qmN.getText());

                                    customDataModel.RunAllCode("C:\\Data\\Qnames\\AllQs.txt");

                                    customDataModel.setModel();



                                } catch (Exception e1) {

                                    // TODO Auto-generated catch block

                                    System.out.println("Error Updating Screen Table" +e1);

                                }

                                try {

                                    customDataModel.fireTableDataChanged();



                                }

                                catch (Exception x){

                                    System.out.println("FireTable Changed Failed" + x);

                                    table.clearSelection();

                                    customDataModel.fireTableDataChanged();

                                }

                            }//while

                        }//void

                    });

                    con.start();

                }

            });

            stop.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e){

                    Thread con1 = new Thread(new Runnable(){

                        public void run()

                        {

                            refresh.setEnabled(true);

                            System.out.println("stopping Thread" );

                            runMode=false;

                            stop.setEnabled(false);

                        }

                    });

                    con1.start();

                }

            });

            clear.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e){

                    System.out.println("txt str is " + txt.getText());

                    if (txt.getText().equals("")){

                        System.out.println("is empty");

                        JFrame done = new JFrame("No Q Specified");

                        JOptionPane.showMessageDialog(done, "Copy and Paste The Qname on the text field");

                    }

                    else

                    {

                        Thread con2 = new Thread(new Runnable(){

                            public void run()

                            {

                                //clear.setEnabled(true);

                                String qq = txt.getText();

                                try {

                                    new ClearQ(qq,state,statusbar,saveNosaveMsg);

                                    // statusbar.setText("Done");

                                } catch (Exception e) {

                                    // TODO Auto-generated catch block

                                    e.printStackTrace();

                                }

                            }

                        });

                        con2.start();

                    }

                }

            });



            JLabel me = new JLabel("Change Environment");

            secondBar.add(me);

            String [] ComboItems={"","PERF101","PERF201","SUP101","QA101","QA202","QA203"};

            selectEnvCombo = new JComboBox(ComboItems);



            selectEnvCombo.addActionListener(new ActionListener(){

                public void actionPerformed(ActionEvent e){

                    JComboBox  cb = (JComboBox)e.getSource();

                    env = (String)cb.getSelectedItem();

                    Thread comboT = new Thread(new Runnable(){

                        public void run(){

                            try {



                                System.out.println("Selected:" + env);

                                customDataModel.CheckMe(env);

                                customDataModel.setMQServerDetails(serverN.getText(),portN.getText(),channelN.getText(),qmN.getText());

                                customDataModel.RunAllCode("C:\\Data\\Qnames\\AllQs.txt");

                                customDataModel.setModel();

                                customDataModel.fireTableDataChanged();

                            } catch (Exception e1) {

                                // TODO Auto-generated catch block

                                e1.printStackTrace();

                            }



                        }//void

                    });

                    comboT.start();

                }

            });

            secondBar.add(selectEnvCombo);

            for (int i=0;i<35;i++){

                secondBar.addSeparator();

            }



            secondBar.add(saveMsg);

            JLabel serverL = new JLabel("HOST:");

            serverBar.add(serverL);

            serverN = new JTextField();

            if (serverN.getText().equals(""))

            {

                serverN.setText("bmusibu1.svr.emea.jpmchase.net");

            }

            serverBar.add(serverN);

            JLabel portL = new JLabel("PORT:");

            serverBar.add(portL);

            portN = new JTextField();

            serverBar.add(portN);

            if (portN.getText().equals(""))

            {

                portN.setText("1414");

            }





            JLabel channelL = new JLabel("CHANNEL:");

            serverBar.add(channelL);

            channelN = new JTextField();

            serverBar.add(channelN);

            if (channelN.getText().equals(""))

            {

                channelN.setText("ACHSIT02.SVRCONN");

            }

            JLabel qmL = new JLabel("QMANAGER:");

            serverBar.add(qmL);

            qmN = new JTextField();

            serverBar.add(qmN);



            if (qmN.getText().equals(""))

            {

                qmN.setText("BMUSIBU1");

            }



            toolbar.setAlignmentX(0);

            secondBar.setAlignmentX(0);

            serverBar.setAlignmentX(0);

            serverBar.setVisible(false);

            JPanel Tpanel = new JPanel();

            Tpanel.setLayout(new BoxLayout(Tpanel, BoxLayout.Y_AXIS));



            Tpanel.add(toolbar);

            Tpanel.add(secondBar);

            Tpanel.add(serverBar);

            scrollPane = new JScrollPane(table);

            topPanel.add(Tpanel,BorderLayout.NORTH);

            topPanel.add( scrollPane, BorderLayout.CENTER );

            //            add(Tpanel, BorderLayout.NORTH);

            //            setLocationRelativeTo(null);

            setLocation(200,100); //middle of the screen



        }



    }

    public void showGUI()

    {



        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            jtableLoad t;

            public void run() {



                try {

                    t = new jtableLoad();

                } catch (Exception e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                }

                t.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                t.setVisible( true );

                t.setSize(t.getPreferredSize());

                t.setSize(1000, 800);



            }

        });



    }



    public static void main(String[] args) throws Exception{



        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

        UIManager.put("ComboBox.selectionBackground", Color.CYAN);

        UIManager.put("ComboBox.selectionForeground", Color.magenta);

        UIManager.put("Button.foreground", Color.blue);

        UIManager.put ("Button.focus", Color.BLACK);

        UIManager.put("Label.foreground", Color.blue);

        UIManager.put("CheckBox.foreground", Color.blue);





        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");



        CheckQs q = new CheckQs();

        //q.RunAllCode();

        q.showGUI();





    }



    @Override

    public void actionPerformed(ActionEvent e) {

        // TODO Auto-generated method stub

        String cmd=e.getActionCommand();

        System.out.println("cmd Pressed is " + cmd);

        if (cmd.equals("Auto Refresh"))

        {

            System.out.println("cmd Pressed is " + cmd);

        }

    }

    public class SelectionListener implements ListSelectionListener {

        JTable table;

        SelectionListener(JTable table) {

            this.table = table;

        }



        public void valueChanged(ListSelectionEvent e) {

            // If cell selection is enabled, both row and column change events are fired

            if (e.getSource() == table.getSelectionModel()

                    && table.getRowSelectionAllowed()) {



                int first = e.getFirstIndex();

                int last = e.getLastIndex();

                System.out.println("rows first " + first);

                System.out.println("rows last " + last);



                if (e.getValueIsAdjusting()==false){

                    System.out.println("CHANGED rows false");

                    int i =table.getSelectedRow();

                    System.out.println("CHANGED new row is " + i);

                    Object obj = table.getValueAt(i, 0);

                    txt.setText((String) obj);

                    clear.setEnabled(true);

                }



            } else if (e.getSource() == table.getColumnModel().getSelectionModel()

                    && table.getColumnSelectionAllowed() ){

                // Row selection changed

                int first = e.getFirstIndex();

                int last = e.getLastIndex();

                System.out.println("Cols first " + first);

                System.out.println("Cols last " + last);

            }



            if (e.getValueIsAdjusting()) {

                // The mouse button has not yet been released

            }

        }



    }



    public String getEnv(){



        return env;

    }



    public String PrintCombo(String val){



        return val;

    }





}