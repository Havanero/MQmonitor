package eurex.clear;

/**
 * Created by carvcal on 28.01.2015.
 */

import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.*;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;


public class CustomDataModel extends AbstractTableModel
{
    String globaRow;
    int iRowIndex=0;
    String fileName;

    CheckQs thisq=new CheckQs();
    private LinkedList<String>list;
    private LinkedList<Integer>qDepth;
    private LinkedList<Integer>maxDepth;
    String[] columnNames;
    int c=0;
    int depth = 0;
    String queueManagerName;
    String queueName;
    String server,CCSID;int port; String channel;
    MQQueue destQueue,outputQueue;
    MQQueueManager queueManager;

    int openOptions=0;
    public Object gettingFull;
    String dataCheck=null;
    String comboSelected=null;
    CheckQs myQs;

    private String serverName,portNummber,channelName,qManager;

    public CustomDataModel() {
        columnNames=new String[]{"MQ Name ","Build Up Count","Max Depth"};
        fileName="C:\\Data\\Qnames\\AllQs.txt";
        System.out.println("simple table started." );

        try {
            RunAllCode(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //getFileData(fileName);
        //System.out.println("main list size " +list.size());
        //System.out.println("row count is " + getRowCount());
        //myQs = new CheckQs();
    }

    public void setModel()throws Exception {
        RunAllCode(fileName);
    }
    public Object getValueAt(int iRowIndex, int iColumnIndex )
    {
        try{
            dataCheck="null";
            if (qDepth.get(iRowIndex)>50){
                dataCheck="High";
                getStatus(iRowIndex);
            }
            else if (qDepth.get(iRowIndex)>10){
                dataCheck="Medium";
                getStatus(iRowIndex);
            }
            else if (qDepth.get(iRowIndex)>1){
                dataCheck="Low";
                getStatus(iRowIndex);
            }
            else
            {
                dataCheck="null";
            }
            switch(iColumnIndex){
                case 0 :return list.get(iRowIndex);
                case 1 :return qDepth.get(iRowIndex);
                case 2 :return maxDepth.get(iRowIndex);
            }
        } catch(Exception s){
            s.printStackTrace();
        }
        return new String();
    }

    public void setValueAt( Object aValue,int iRowIndex,int iColumnIndex ){}

    // Return 0 because we handle our own columns
    public int getColumnCount() { return columnNames.length; }

    public int getRowCount() { return list.size();}
    public String getColumnName(int col) { return columnNames[col]; }

    public void getFileData(String fname)
    {
        if(fname.equals(null)) fname="C:\\Data\\Qnames\\AllQs.txt";

        File file =new File(fname);
        Scanner input=null;
        list =new LinkedList<String>();
        qDepth =new LinkedList<Integer>();
        maxDepth =new LinkedList<Integer>();
        list.clear();
        qDepth.clear();
        maxDepth.clear();
        System.out.println("env inside customtable" );
        try{
            input =new Scanner(file);
            System.out.println("get File Data started" + fname);
            while(input.hasNext()){
                String record = input.next();

                if (comboSelected!=null)
                {
                    String findEnv=CorrolateString(record,".",".");
                    String myCombo=record.replaceAll(findEnv,comboSelected);
                    System.out.println("replacing " + findEnv +"whith " + myCombo);
                    record=myCombo;
                }

                list.add(record);

            }
            input.close();
        }catch(Exception x){
            JFrame fileLoad =new JFrame("Loading File");
            JOptionPane.showMessageDialog(fileLoad,"Missing Data File..Please Create C:\\data\\Qnames\\AllQs.tx");
            fileLoad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            System.exit(1);
        }
    }
    public void MQConnect() throws Exception
    {
        System.out.println("MQ connect");
        System.out.println("Using Server " +serverName
                +"PortNumber:" +portNummber + "CHANNEL:"
                +channelName +"QM" +qManager);
        if (serverName==null)
        {
            System.out.println("needs set up  is empty");
            serverName="bmusibu1.svr.emea.jpmchase.net";
            portNummber="1414";
            channelName="ACHSIT02.SVRCONN";
            qManager="BMUSIBU1";

        }
        else
        {
            System.out.println("NOT NULL");
        }

        MQEnvironment.hostname=serverName;
        //MQEnvironment.port =1414;
        MQEnvironment.port =Integer.parseInt(portNummber);
        //MQEnvironment.channel = "ACHSIT02.SVRCONN";
        MQEnvironment.channel = channelName;
        MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);
        //this.queueManagerName = "BMUSIBU1";
        this.queueManagerName = qManager;

        System.out.println("Connecting to Queue Manager " +queueManagerName);
        queueManager =new MQQueueManager(queueManagerName);
        openOptions = MQC.MQOO_INQUIRE;
        System.out.println("Successfully Connected to  " +queueManagerName);
    }

    public void getQCount(String localQ)throws Exception
    {
        try{
            gettingFull=0;
            this.queueName=localQ;
            System.out.println("Getting Values for Qname " +queueName);
            destQueue =queueManager.accessQueue(queueName,openOptions);
            depth=destQueue.getCurrentDepth();
            int maxDep=destQueue.getMaximumDepth();
            System.out.println(localQ + " |Queue Depth == " +depth);
            qDepth.add(depth);
            maxDepth.add(maxDep);
            System.out.println("ADDED DEPTH");
        }catch(MQException x)
        {
            if (x.reasonCode==x.MQRC_HANDLE_NOT_AVAILABLE) {
                System.out.println("Handler Error ..." + x);
                MQDisconnect();
                MQConnect();
                getQCount(queueName);
            }
            System.out.println("CheckQerro" + x);
        }
    }

    public void MQDisconnect() throws Exception
    {
        try {
            System.out.println("CLOSING CONNECTION MANAGER");
            // System.out.println("REMOVED " + c + " MSGS FROM " + queueName + " Queue");
            destQueue.close();
            queueManager.disconnect();
        }catch (MQException ex)
        {
            ex.printStackTrace();
        }
    }

    public void RunAllCode(String fileName)throws Exception
    {
        getFileData(fileName);
        System.out.println("data List is " +list.size());
        MQConnect();
        for(int i=0;i<list.size();i++){
            try {
                getQCount(list.get(i));

            }catch (Exception e) {
                System.out.println("Error Checking Qname " +list.get(i) +"..." +e);
            }
        }
//MQDisconnect();
    }

    public Object getStatus(int row) {
        // TODO Auto-generated method stub

        String val=dataCheck;
        //System.out.println("setting up DATA ==" + val);
        return val;
    }

    public String CorrolateString(String fullString, String leftOf,String rightOf)
    {
        String finalStr="";
        int localOfString=0;
        int rightStringInt=0;
        try{
            localOfString=fullString.indexOf(leftOf);
            rightStringInt=fullString.indexOf(rightOf);
            finalStr=fullString.substring(localOfString+leftOf.length(),rightStringInt);
        }
        catch(java.lang.StringIndexOutOfBoundsException x)
        {
            //    System.out.println(x.getCause());
            localOfString=fullString.indexOf(leftOf);
            String nextVal=fullString.substring(0,localOfString);
            System.out.println(localOfString + nextVal);
            String reSearch="";
            reSearch=nextVal+leftOf;
            System.out.println(reSearch);
            localOfString=fullString.indexOf(reSearch);
            int lastIndex = fullString.indexOf(rightOf,reSearch.length());
            System.out.println(lastIndex);
            finalStr=fullString.substring(localOfString+reSearch.length(),lastIndex);
        }

        return finalStr.trim();
    }
    public String CheckMe(String selectedEnv){

        //CheckQs chk = new CheckQs();
        //chk.new getStringEnv();
        //Java fix x.new y new etx...
        //CheckQs chk = new CheckQs();
        // CheckQs.getStringEnv hs = chk.new getStringEnv();
        System.out.println("from data table");
        // tb.actionPerformed(ActionEvent e);

        if (selectedEnv=="")
        {
            selectedEnv=null;
        }
        comboSelected=selectedEnv;
        System.out.println("combo start");
        System.out.println("selectedEnv is now ==" + selectedEnv);
        // String type = chk.getValueFromCombo();
        System.out.println("combo end" );
        return selectedEnv;
    }

    public void setMQServerDetails(String serverName,String portNummber,String channel,String qManager)
    {
        this.serverName=serverName;
        this.portNummber=portNummber;
        this.channelName=channel;
        this.qManager=qManager;
        System.out.println("outputting server details =" + serverName + portNummber + channel + qManager);

    }

    class PrintClass {
        public void printSomeStuffs(String type) {
            if(type !=null)
                System.out.println("This is selected type " +type);
        }

    }

}