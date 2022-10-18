/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package helloworld;

import Environment.Environment;
import ai.Choice;

/**
 *
 * @author atrop
 */
public class STF extends AT_ST{
    
    @Override
    public void setup(){
        super.setup();
        A.addChoice(new Choice("UP"));
        A.addChoice(new Choice("DOWN"));
    }
    
    @Override
    public Status MyJoinSession(){
        
        this.DFAddMyServices(new String[]{"TYPE STF"});
        
        outbox=session.createReply();
        outbox.setContent("Request join session "+ sessionKey);
        LARVAsend(outbox);
        
        session = LARVAblockingReceive();
        
        //myAssistedNavigation(37, 13);
        
        return Status.SOLVEPROBLEM;
    }
    
    @Override
    protected double U(Environment E, Choice a){
        if(E.getDistance()>0 && E.getGround()<25 && E.getGPS().getZ() < E.getMaxlevel()){
            if(a.getName().equals("UP")){
                return Choice.ANY_VALUE;
            }else{
                return Choice.MAX_UTILITY;
            }
        }else if(E.getDistance() == 0 && E.getGround()>0){
            if(a.getName().equals("DOWN")){
                return Choice.ANY_VALUE;
            }else{
                return Choice.MAX_UTILITY;
            }
        }else if(!isFreeFront()){
            if(a.getName().equals("RIGHT")){
                return Choice.ANY_VALUE;
            }else{
                return Choice.MAX_UTILITY;
            }
        }else{
            if(a.getName().equals("MOVE")){
                return U(S(E,a));
            }else if (a.getName().equals("LEFT") || a.getName().equals("RIGTH")){
                return U(S(S(E,a),new Choice("MOVE")));
            }else{
                return Choice.MAX_UTILITY;
            }
        }
        
    }
    
}
