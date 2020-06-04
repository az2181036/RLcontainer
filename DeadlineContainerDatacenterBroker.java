package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.container.core.ContainerDatacenterBroker;
import org.cloudbus.cloudsim.container.lists.ContainerList;
import org.cloudbus.cloudsim.lists.CloudletList;

import java.util.List;

public class DeadlineContainerDatacenterBroker extends ContainerDatacenterBroker {
    protected List<DeadlineContainer> containerList;

    public DeadlineContainerDatacenterBroker(String name, double overBookingfactor) throws Exception{
        super(name, overBookingfactor);
    }

    public void bindCloudletToContainer(int cloudletId, int containerId) {
        DeadlineCloudlet cloudlet = CloudletList.getById(getCloudletList(), cloudletId);
        cloudlet.setContainerId(containerId);
        ContainerList.getById(getContainerList(),containerId).setFinishTime(cloudlet.getCloudletLength());
    }

    public List<DeadlineContainer> getContainerList() {
        return this.containerList;
    }
}
