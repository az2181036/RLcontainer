package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.container.containerProvisioners.ContainerBwProvisioner;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerPe;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerRamProvisioner;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.schedulers.ContainerScheduler;

import java.util.ArrayList;
import java.util.List;

public class DeadlineContainerVm extends ContainerVm {
    private final List<DeadlineContainer> containerList = new ArrayList<>();
    public DeadlineContainerVm(int id,
                               int userId,
                               double mips,
                               float ram,
                               long bw,
                               long size,
                               String vmm,
                               ContainerScheduler containerScheduler,
                               ContainerRamProvisioner containerRamProvisioner,
                               ContainerBwProvisioner containerBwProvisioner,
                               List<? extends ContainerPe> peList){
        super(id, userId, mips, ram, bw, size, vmm, containerScheduler, containerRamProvisioner, containerBwProvisioner, peList);
    }

    public List<DeadlineContainer> getContainerList() {
        return this.containerList;
    }
}
