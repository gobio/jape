package eu.gobio.jape;

import com.sun.tools.attach.VirtualMachine;
import org.aspectj.weaver.loadtime.Agent;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URI;

public class Jape {
    public static void install() {
        try {
            VirtualMachine vm = VirtualMachine.attach(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            URI weaver = Agent.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            vm.loadAgent(new File(weaver).getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
