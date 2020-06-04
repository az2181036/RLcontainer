package org.cloudbus.cloudsim.examples.RLcontainer;

import cern.colt.matrix.DoubleMatrix1D;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

public class PowerContainerAllocationPolicyRL extends PowerContainerAllocationPolicy {

    private int nb_epoch;
    private double gamma;
    private double epsilon_rate;
    private List <Double> epsilon_lst;
    private List<DeadlineContainerVm> containerVmList;
    private HashMap<ArrayList, ArrayList> Q;
    private HashMap<ArrayList, ArrayList> memory;

    public PowerContainerAllocationPolicyRL(){
        super();
    }

    public DeadlineContainerVm findVmForContainer(DeadlineContainer container) {
        ArrayList S = new ArrayList();
        for (DeadlineContainerVm containerVm : getContainerVmList()) {
            int multiplicator = 0;
            if (containerVm.isSuitableForContainer(container)) {
                if (containerVm.updateContainersProcessing(CloudSim.clock()) + container.getFinishTime() <= container.getDeadline()) {
                    multiplicator = 1;
                }
            }

            S.add(multiplicator);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Container> containerList) {
        return null;
    }

    public List<DeadlineContainerVm> getContainerVmList() {
        return this.containerVmList;
    }

    @Override
    public void deallocateVmForContainer(Container container) {
        ContainerVm containerVm = getContainerTable().remove(container.getUid());
        if (containerVm != null) {
            containerVm.containerDestroy(container);
        }
    }
}
