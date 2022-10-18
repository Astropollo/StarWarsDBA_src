package helloworld;

import appboot.LARVABoot;
import static crypto.Keygen.getHexaKey;

public class Main {

    public static void main(String[] args) {
        LARVABoot boot = new LARVABoot();
        boot.Boot("isg2.ugr.es", 1099);
        boot.launchAgent("ATST-"+getHexaKey(4), AT_ST.class);
        boot.WaitToShutDown();
    }
    
}
