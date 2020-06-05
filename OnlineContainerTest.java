package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerPe;
import org.cloudbus.cloudsim.container.containerProvisioners.ContainerRamProvisionerSimple;
import org.cloudbus.cloudsim.container.containerProvisioners.CotainerPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmBwProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPe;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmPeProvisionerSimple;
import org.cloudbus.cloudsim.container.containerVmProvisioners.ContainerVmRamProvisionerSimple;
import org.cloudbus.cloudsim.container.core.*;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicy;
import org.cloudbus.cloudsim.container.hostSelectionPolicies.HostSelectionPolicyFirstFit;
import org.cloudbus.cloudsim.container.resourceAllocatorMigrationEnabled.PowerContainerVmAllocationPolicyMigrationAbstractHostSelection;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerVmAllocationPolicy;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerAllocationPolicySimple;
import org.cloudbus.cloudsim.container.schedulers.ContainerCloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.container.schedulers.ContainerSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.schedulers.ContainerVmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.container.utils.IDs;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicy;
import org.cloudbus.cloudsim.container.vmSelectionPolicies.PowerContainerVmSelectionPolicyMaximumUsage;
import org.cloudbus.cloudsim.core.CloudSim;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class OnlineContainerTest {
    private static List<DeadlineCloudlet> cloudletList;
    private static List<ContainerVm> vmList;
    private static List<DeadlineContainer> containerList;
    private static List<ContainerHost> hostList;

    public static void main(String[] args) {
        Log.printLine("Starting Online Container Test...");
        try {
            int num_user = 1;
            Calendar calendar = Calendar.getInstance(); // The fields of calender have been initialized with the current date and time.
            boolean trace_flag = false; //Deactivating the event tracing
            CloudSim.init(num_user, calendar, trace_flag); //1- Like CloudSim the first step is initializing the CloudSim Package before creating any entities.

            PowerContainerVmSelectionPolicy vmSelectionPolicy = new PowerContainerVmSelectionPolicyMaximumUsage(); //VM selection Policy for migration
            HostSelectionPolicy hostSelectionPolicy = new HostSelectionPolicyFirstFit(); // Host selection being destination of migration

            // Host utilization threshold
            double overUtilizationThreshold = 1;
            double underUtilizationThreshold = 0;

            hostList = new ArrayList<ContainerHost>();
            hostList = createHostList(Constants.NUMBER_HOSTS);
            cloudletList = new ArrayList<DeadlineCloudlet>();
            vmList = new ArrayList<ContainerVm>();

            // VMs to Host
            ContainerVmAllocationPolicy vmAllocationPolicy = new
                    PowerContainerVmAllocationPolicyMigrationAbstractHostSelection(hostList, vmSelectionPolicy,
                    hostSelectionPolicy, overUtilizationThreshold, underUtilizationThreshold);

            // The overbooking factor for allocating containers to VMs. This factor is used by the broker for the allocation process.
            int overBookingFactor = 80;
            ContainerDatacenterBroker broker = createBroker(overBookingFactor);
            int brokerId = broker.getId();

            cloudletList = createDeadlineCloudletList(brokerId, Constants.NUMBER_CLOUDLETS);
            containerList = createDeadlineContainerList(brokerId, Constants.NUMBER_CLOUDLETS);
            vmList = createVmList(brokerId, Constants.NUMBER_VMS);

            PowerContainerAllocationPolicyRL containerAllocationPolicy = new PowerContainerAllocationPolicyRL(Constants.SEED,
                    Constants.GAMMA, Constants.EPSILON, Constants.EPSILONRATE, Constants.FINALEPSILON,
                    Constants.NUMBEREPOCH, containerList); //Container to VMs

            String logAddress = "~/Results";

            @SuppressWarnings("unused")
            PowerContainerDatacenter e = (PowerContainerDatacenter) createDatacenter("datacenter",
                    PowerContainerDatacenterCM.class, hostList, vmAllocationPolicy, containerAllocationPolicy,
                    getExperimentName("Online Container Test", String.valueOf(overBookingFactor)),
                    Constants.SCHEDULING_INTERVAL, logAddress,
                    Constants.VM_STARTTUP_DELAY, Constants.CONTAINER_STARTTUP_DELAY);

            broker.submitCloudletList(cloudletList.subList(0, containerList.size()));
            broker.submitContainerList(containerList);
            broker.submitVmList(vmList);

            for (DeadlineCloudlet cloudlet:cloudletList){
                int id = cloudlet.getCloudletId();
                broker.bindCloudletToContainer(id, id);
            }

            CloudSim.terminateSimulation(86400.00); //set terminate time


            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            List<DeadlineCloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
            Log.printLine("ContainerCloudSimExample1 finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happen");
        }
    }

    private static String getExperimentName(String... args) {
        StringBuilder experimentName = new StringBuilder();

        for (int i = 0; i < args.length; ++i) {
            if (!args[i].isEmpty()) {
                if (i != 0) {
                    experimentName.append("_");
                }

                experimentName.append(args[i]);
            }
        }

        return experimentName.toString();
    }

    /**
     * Creates the broker.
     *
     * @param overBookingFactor
     * @return the datacenter broker
     */
    private static ContainerDatacenterBroker createBroker(int overBookingFactor) {

        ContainerDatacenterBroker broker = null;

        try {
            broker = new ContainerDatacenterBroker("Broker", overBookingFactor);
        } catch (Exception var2) {
            var2.printStackTrace();
            System.exit(0);
        }

        return broker;
    }

    /**
     * Prints the Cloudlet objects.
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<DeadlineCloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
                + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatusString() == "Success") {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId()
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent
                        + dft.format(cloudlet.getActualCPUTime()) + indent
                        + indent + dft.format(cloudlet.getExecStartTime())
                        + indent + indent
                        + dft.format(cloudlet.getFinishTime()));
            }
        }
    }

    /**
     * Create the Virtual machines and add them to the list
     *
     * @param brokerId
     * @param containerVmsNumber
     */
    private static ArrayList<ContainerVm> createVmList(int brokerId, int containerVmsNumber) {
        ArrayList<ContainerVm> containerVms = new ArrayList<ContainerVm>();

        for (int i = 0; i < containerVmsNumber; ++i) {
            ArrayList<ContainerPe> peList = new ArrayList<ContainerPe>();
            int vmType = 0;
            for (int j = 0; j < Constants.VM_PES[vmType]; ++j) {
                peList.add(new ContainerPe(j,
                        new CotainerPeProvisionerSimple((double) Constants.VM_MIPS[vmType])));
            }
            containerVms.add(new PowerContainerVm(IDs.pollId(ContainerVm.class), brokerId,
                    (double) Constants.VM_MIPS[vmType], (float) Constants.VM_RAM[vmType],
                    Constants.VM_BW, Constants.VM_SIZE, "Xen",
                    new ContainerSchedulerTimeSharedOverSubscription(peList),
                    new ContainerRamProvisionerSimple(Constants.VM_RAM[vmType]),
                    new ContainerBwProvisionerSimple(Constants.VM_BW),
                    peList, Constants.SCHEDULING_INTERVAL));
        }
        return containerVms;
    }

    /**
     * Create the host list considering the specs listed in the {@link Constants}.
     *
     * @param hostsNumber
     * @return
     */
    public static List<ContainerHost> createHostList(int hostsNumber) {
        ArrayList<ContainerHost> hostList = new ArrayList<ContainerHost>();
        for (int i = 0; i < hostsNumber; ++i) {
            int hostType = 0;
            ArrayList<ContainerVmPe> peList = new ArrayList<ContainerVmPe>();
            for (int j = 0; j < Constants.HOST_PES[hostType]; ++j) {
                peList.add(new ContainerVmPe(j,
                        new ContainerVmPeProvisionerSimple((double) Constants.HOST_MIPS[hostType])));
            }

            hostList.add(new PowerContainerHostUtilizationHistory(IDs.pollId(ContainerHost.class),
                    new ContainerVmRamProvisionerSimple(Constants.HOST_RAM[hostType]),
                    new ContainerVmBwProvisionerSimple(1000000L), 1000000L, peList,
                    new ContainerVmSchedulerTimeSharedOverSubscription(peList),
                    Constants.HOST_POWER[hostType]));
        }

        return hostList;
    }


    /**
     * Create the data center
     *
     * @param name
     * @param datacenterClass
     * @param hostList
     * @param vmAllocationPolicy
     * @param containerAllocationPolicy
     * @param experimentName
     * @param logAddress
     * @return
     * @throws Exception
     */
    public static ContainerDatacenter createDatacenter(String name, Class<? extends ContainerDatacenter> datacenterClass,
                                                       List<ContainerHost> hostList,
                                                       ContainerVmAllocationPolicy vmAllocationPolicy,
                                                       PowerContainerAllocationPolicyRL containerAllocationPolicy,
                                                       String experimentName, double schedulingInterval, String logAddress, double VMStartupDelay,
                                                       double ContainerStartupDelay) throws Exception {
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0D;
        double cost = 3.0D;
        double costPerMem = 0.05D;
        double costPerStorage = 0.001D;
        double costPerBw = 0.0D;
        ContainerDatacenterCharacteristics characteristics = new
                ContainerDatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage,
                costPerBw);
        ContainerDatacenter datacenter = new PowerContainerDatacenterCM(name, characteristics, vmAllocationPolicy,
                containerAllocationPolicy, new LinkedList<Storage>(), schedulingInterval, experimentName, logAddress,
                VMStartupDelay, ContainerStartupDelay);

        return datacenter;
    }

    /**
     * create the containers for hosting the cloudlets and binding them together.
     *
     * @param brokerId
     * @param containersNumber
     * @return
     */
    public static List<DeadlineContainer> createDeadlineContainerList(int brokerId, int containersNumber) {
        ArrayList<DeadlineContainer> containers = new ArrayList<DeadlineContainer>();

        for (int i = 0; i < containersNumber; ++i) {
            int containerType = 0;

            containers.add(new DeadlineContainer(i, brokerId, (double) Constants.CONTAINER_MIPS[containerType], Constants.
                    CONTAINER_PES[containerType], Constants.CONTAINER_RAM[containerType], Constants.CONTAINER_BW, 0L, "Xen",
                    new ContainerCloudletSchedulerDynamicWorkload(Constants.CONTAINER_MIPS[containerType], Constants.CONTAINER_PES[containerType]), Constants.SCHEDULING_INTERVAL));
        }

        return containers;
    }

    /**
     * Creating the cloudlet list that are going to run on containers
     *
     * @param brokerId
     * @param numberOfCloudlets
     * @return
     * @throws FileNotFoundException
     */
    public static List<DeadlineCloudlet> createDeadlineCloudletList(int brokerId, int numberOfCloudlets)
            throws FileNotFoundException {
        long filesize = 0;
        long outsize = 0;
        UtilizationModel utilizationModelFull = new UtilizationModelFull();
        UtilizationModel utilizationModelNull = new UtilizationModelNull();
        ArrayList<DeadlineCloudlet> cloudletList = new ArrayList<DeadlineCloudlet>();
        for (int i=0; i<numberOfCloudlets; i++){
            DeadlineCloudlet cloudlet = new DeadlineCloudlet(i, Constants.CLOUDLET_LENGTH, Constants.CLOUDLET_PES, filesize, outsize,
                    utilizationModelFull, utilizationModelFull, utilizationModelNull);
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }

}
