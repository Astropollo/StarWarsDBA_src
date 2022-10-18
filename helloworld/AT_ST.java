
package helloworld;

import Environment.Environment;
import ai.Choice;
import ai.DecisionSet;

public class AT_ST extends AgentLARVAFull{
    
    String problems[]={
        "SandboxTesting",
        "FlatNorth",
        "FlatNorthWest",
        "FlatSouth",
        "Bumpy0",
        "Bumpy1",
        "Bumpy2",
        "Bumpy3",
        "Bumpy4",
        "Halfmoon1",
        "Halfmoon3"
    };
    
    Choice action;
    
    @Override
    public void setup(){
        enableDeepLARVAMonitoring();
        super.setup();
        
        this.setupEnvironment();
        A = new DecisionSet();
        A.addChoice(new Choice("MOVE"));
        A.addChoice(new Choice("RIGHT"));
        A.addChoice(new Choice("LEFT"));
    }
    
    public boolean isFreeFront(){
        int visualHere, visualFront, slopeFront;
        visualHere = E.getPolarVisual()[0][0];
        visualFront= E.getPolarVisual()[2][1];
        slopeFront = visualHere-visualFront;
        return  visualFront >= E.getMinlevel()
                && visualFront <= E.getMaxlevel()
                && slopeFront <= E.getMaxslope()
                && slopeFront >= -E.getMaxslope()
                && E.getGPS().getX()>= 0 
                && E.getGPS().getX() < E.getWorldWidth();
    }
    
    @Override
    protected double U(Environment E, Choice a){
        if(!isFreeFront()){
            if(a.getName().equals("RIGHT")){
                return Choice.ANY_VALUE;
            }else{
                return Choice.MAX_UTILITY;
            }
        }else{
            if(a.getName().equals("MOVE")){
                return U(S(E,a));
            }else{
                return U(S(S(E,a),new Choice("MOVE")));
            }
        }
        
    }
    
    @Override
    public void Execute(){
        Info("Status: " + myStatus.name());
        switch (myStatus) {
            case START:
                problem = inputSelect("Please select the problem", problems,"");
                myStatus = Status.CHECKIN;
                break;
            case CHECKIN:
                // The execution of a state (as a method) returns
                // the next state
                myStatus = MyCheckin();
                break;
            case OPENPROBLEM:
                myStatus = MyOpenProblem();
                break;
            case JOINSESSION:
                myStatus = MyJoinSession();
                break;
            case SOLVEPROBLEM:
                myStatus = MySolveProblem();
                break;
            case CLOSEPROBLEM:
                myStatus = MyCloseProblem();
                break;
            case CHECKOUT:
                myStatus = MyCheckout();
                break;
            case EXIT:
            default:
                doExit();
           
        }
    }
    
    @Override
    public Status MySolveProblem(){
        MyReadPerceptions();
        
        if(G(E)){
            Message("Ole tu ");
            return Status.CLOSEPROBLEM;
        }
        if(!Ve(E)){
            Alert("putamae");
            return Status.CLOSEPROBLEM;
        }
        action = Ag(E,A);
        if(action == null){
            Alert("Y ahora pa donde");
            return Status.CLOSEPROBLEM;
        }
        
        MyExecuteAction(action.getName());
        return Status.SOLVEPROBLEM;
        
        
        /*
        
        String a = inputSelect("Please select next action", A.getAllChoiceNames(), "");
        if(a==null){
            return Status.CLOSEPROBLEM;
        }
        
        action = new Choice(a);
        */
    }
    
    public Status MyJoinSession(){
        
        this.DFAddMyServices(new String[]{"TYPE AT_ST"});
        
        outbox=session.createReply();
        outbox.setContent("Request join session "+ sessionKey);
        LARVAsend(outbox);
        
        session = LARVAblockingReceive();
        
        //myAssistedNavigation(37, 13);
        
        return Status.SOLVEPROBLEM;
    }
    
    protected Status myAssistedNavigation(int goalx, int goaly) {
        Info("Requesting course to " + goalx + " " + goaly);
        outbox = session.createReply();
        outbox.setContent("Request course to " + goalx + " " + goaly + " Session " + sessionKey);
        this.LARVAsend(outbox);
        session = this.LARVAblockingReceive();
        getEnvironment().setExternalPerceptions(session.getContent());
        return Status.CHECKIN.SOLVEPROBLEM;
    }

    // 100% New method to execute an action
    protected boolean MyExecuteAction(String action) {
        Info("Executing action " + action);
        outbox = session.createReply();
        // Remember to include sessionID in all communications
        outbox.setContent("Request execute " + action + " session " + sessionKey);
        this.LARVAsend(outbox);
        session = this.LARVAblockingReceive();
        if (!session.getContent().startsWith("Inform")) {
            Error("Unable to execute action " + action + " due to " + session.getContent());
            return false;
        }
        return true;
    }

    // Read perceptions and send them directly to the Environment instance,
    // so we can query any items of sensors and added-value information
    protected boolean MyReadPerceptions() {
        Info("Reading perceptions");
        outbox = session.createReply();
        outbox.setContent("Query sensors session " + sessionKey);
        this.LARVAsend(outbox);
        //this.myEnergy++;
        session = this.LARVAblockingReceive();
        if (session.getContent().startsWith("Failure")) {
            Error("Unable to read perceptions due to " + session.getContent());
            return false;
        }
        getEnvironment().setExternalPerceptions(session.getContent());
        //Info(this.easyPrintPerceptions());
        return true;
    }
}
