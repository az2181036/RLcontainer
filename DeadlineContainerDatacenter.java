package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.container.core.ContainerDatacenter;
import org.cloudbus.cloudsim.container.core.ContainerDatacenterCharacteristics;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;

import java.util.List;

public class DeadlineContainerDatacenter extends ContainerDatacenter {
    public DeadlineContainerDatacenter(
            String name,
            ContainerDatacenterCharacteristics characteristics,
            ContainerVmAllocationPolicy vmAllocationPolicy,
            ContainerAllocationPolicy containerAllocationPolicy,
            List<Storage> storageList,
            double schedulingInterval, String experimentName, String logAddress) throws Exception {
        super(name, characteristics, vmAllocationPolicy, containerAllocationPolicy,
                storageList, schedulingInterval, experimentName, logAddress);
    }
}
