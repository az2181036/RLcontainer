package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.lists.ContainerVmList;
import org.cloudbus.cloudsim.container.resourceAllocators.ContainerAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class PowerContainerAllocationPolicyRL extends ContainerAllocationPolicy {

    private int numberEpoch;
    private double gamma;
    private double epsilon;
    private double finalEpsilon;
    private double epsilonRate;
    private Random random;
    private List<Double> simulationRam;
    private List<Double> simulationCPU;
    private HashMap<List<Integer>, double[]> Q;
    private List<DeadlineContainer> containerList;
    private List<DeadlineContainerVm> containerVmList;
    private final Map<String, DeadlineContainerVm> containerVmTable = new HashMap<>();

    public PowerContainerAllocationPolicyRL(
            int seed,
            double gamma,
            double epsilon,
            double epsilonRate,
            double finalEpsilon,
            int numberEpoch,
            List<DeadlineContainer> containerList){
        setRandom(seed);
        setGamma(gamma);
        setEpsilon(epsilon);
        setEpsilonRate(epsilonRate);
        setFinalEpsilon(finalEpsilon);
        setNumberEpoch(numberEpoch);
        setContainerList(containerList);

        setQ(new HashMap<>());
        setSimulationCPU(new ArrayList<>());
        setSimulationCPU(new ArrayList<>());
        setContainerVmList(new ArrayList<>());
    }

    public boolean allocateVmForContainer(DeadlineContainer container, List<DeadlineContainerVm> containerVmList) {
        setContainerVmList(containerVmList);
        return allocateVmForContainer(container, findVmForContainer(container));
    }

    public boolean allocateVmForContainer(DeadlineContainer container, DeadlineContainerVm containerVm) {
        if (containerVm == null) {
            Log.formatLine("%.2f: No suitable VM found for Container#" + container.getId() + "\n", CloudSim.clock());
            return false;
        }
        if (containerVm.containerCreate(container)) { // if vm has been succesfully created in the host
            getContainerVmTable().put(container.getUid(), containerVm);
            container.setVm(containerVm);
            Log.formatLine(
                    "%.2f: Container #" + container.getId() + " has been allocated to the VM #" + containerVm.getId(),
                    CloudSim.clock());
            return true;
        }
        Log.formatLine(
                "%.2f: Creation of Container #" + container.getId() + " on the Vm #" + containerVm.getId() + " failed\n",
                CloudSim.clock());
        return false;
    }

    public DeadlineContainerVm findVmForContainer(DeadlineContainer container) {
        setSimulationRam();
        setSimulationCPU();
        List <Integer> S = computeS(container);

        if(getQ().containsKey(S)){
            double[] q = getQ().get(S);
            return ContainerVmList.getById(getContainerVmList(),getMaxIdx(q));
        }
        return ContainerVmList.getById(getContainerVmList(), getRandom().nextInt(getContainerVmList().size()));
    }

    public void deallocateVmForContainer(DeadlineContainer container) {
        DeadlineContainerVm containerVm = getContainerVmTable().remove(container.getUid());
        if (containerVm != null) {
            containerVm.containerDestroy(container);
        }
    }

    public DeadlineContainerVm getContainerVm(DeadlineContainer container) {
        return getContainerVmTable().get(container.getUid());
    }

    public DeadlineContainerVm getContainerVm(int containerId, int userId) {
        return getContainerVmTable().get(DeadlineContainer.getUid(userId, containerId));
    }

    public void RLTrain(){
        setSimulationRam();
        setSimulationCPU();
        for(int epoch=0;epoch<getNumberEpoch();epoch++){
            List <Integer> S = computeS(getContainerList().get(0));
            for(int i=0;i<getContainerList().size();i++){
                DeadlineContainer container = getContainerList().get(i);

                int action;
                double[] q = new double[getContainerVmList().size()];
                if(getQ().containsKey(S)) q = getQ().get(S);

                if (getRandom().nextDouble() < getEpsilon())
                    action = getRandom().nextInt(getContainerVmList().size());
                else action = getMaxIdx(q);

                double reward = computeReward(action, S);
                updateSimulationCPU(action, container);
                updateSimulationRam(action, container);

                List <Integer> _S;
                double[] _q = new double[getContainerVmList().size()];
                if(i < getContainerList().size()-1){
                    _S = computeS(getContainerList().get(i+1));
                    if(getQ().containsKey(_S)) _q = getQ().get(_S);
                    S = _S;
                }
                computeQ(q, action, reward, _q);
                putQ(S, q);
            }
        }
    }

    public int getMaxIdx(double[] arr){
        if(arr==null||arr.length==0)
            return -1;
        int idx=0;
        for(int i =0;i<arr.length-1;i++){
            if(arr[idx]>arr[i+1]){
                idx=i+1;
            }
        }
        return idx;
    }

    public double computeReward(int action, List <Integer> S){
        if(S.get(action) == 0){
            return -1;
        }else{
            return 1 / S.get(action);
        }
    }

    public int computeSValue(int i, DeadlineContainer container){
        int multiplicator = 0;
        DeadlineContainerVm containerVm = getContainerVmList().get(i);
        if (containerVm.isSuitableForContainer(container))
            if (containerVm.updateContainersProcessing(CloudSim.clock())+container.getFinishTime()<=container.getDeadline())
                multiplicator = 1;
        double tmp = Math.max(Math.floor(getSimulationCPU().get(i)/container.getCurrentRequestedTotalMips()),
                Math.floor(getSimulationRam().get(i)/ container.getRam()));
        return (int)(multiplicator * tmp);
    }

    public List<Integer> computeS(DeadlineContainer container){
        ArrayList<Integer> S = new ArrayList<>();
        for (int i=0;i<getContainerVmList().size();i++) S.add(computeSValue(i, container));
        return S;
    }

    public List<Double> getSimulationCPU(){
        return this.simulationCPU;
    }

    public void setSimulationCPU(){
        this.simulationCPU.clear();
        for (DeadlineContainerVm containerVm : getContainerVmList()) {
            this.simulationCPU.add(containerVm.getMaxAvailableMips());
        }
    }

    public void setSimulationCPU(List<Double> simulationCPU) {
        this.simulationCPU = simulationCPU;
    }

    public void updateSimulationCPU(int i, DeadlineContainer container){
        this.simulationCPU.set(i, this.simulationCPU.get(i)-container.getCurrentRequestedTotalMips());
    }

    public List<Double> getSimulationRam(){
        return this.simulationRam;
    }

    public void setSimulationRam(){
        this.simulationRam.clear();
        for (DeadlineContainerVm containerVm : getContainerVmList()) {
            this.simulationRam.add((double)(containerVm.getRam() - containerVm.getCurrentAllocatedRam()));
        }
    }

    public void setSimulationRam(List<Double> simulationRam) {
        this.simulationRam = simulationRam;
    }

    public void updateSimulationRam(int i, DeadlineContainer container){
        this.simulationRam.set(i, this.simulationRam.get(i)-container.getRam());
    }

    public double getFinalEpsilon() {
        return finalEpsilon;
    }

    public void setFinalEpsilon(double finalEpsilon) {
        this.finalEpsilon = finalEpsilon;
    }

    public double getEpsilonRate() {
        return epsilonRate;
    }

    public void setEpsilonRate(double epsilonRate) {
        this.epsilonRate = epsilonRate;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public Map<String, DeadlineContainerVm> getContainerVmTable() {
        return containerVmTable;
    }

    public double getGamma(){
        return this.gamma;
    }
    public void setGamma(double gamma){
        this.gamma = gamma;
    }

    public HashMap<List<Integer>, double[]> getQ(){
        return this.Q;
    }

    public void setQ(HashMap<List<Integer>, double[]> q) {
        this.Q = q;
    }

    public void putQ(List<Integer> S, double[] q){
        this.Q.put(S, q);
    }
    public void computeQ(double[] q, int action, double reword, double[] _q){
        q[action] = (1 - getGamma()) * q[action] + getGamma() * (reword + Arrays.stream(_q).max().getAsDouble());
    }

    public int getNumberEpoch(){
        return numberEpoch;
    }
    public void setNumberEpoch(int numberEpoch){
        this.numberEpoch = numberEpoch;
    }

    public List<DeadlineContainer> getContainerList(){
        return this.containerList;
    }
    public void setContainerList(List<DeadlineContainer> containerList){
        this.containerList = containerList;
    }

    public Random getRandom(){
        return this.random;
    }
    public void setRandom(int seed){
        this.random = new Random(seed);
    }
    public void setRandom(Random random) {
        this.random = random;
    }

    public List<DeadlineContainerVm> getContainerVmList(){
        return containerVmList;
    }

    public boolean allocateVmForContainer(Container container, List<ContainerVm> containerVmList){
        return false;
    }

    public boolean allocateVmForContainer(Container container, ContainerVm vm){
        return false;
    }

    public List<Map<String, Object>> optimizeAllocation(List<? extends Container> containerList){
        return null;
    }

    public void deallocateVmForContainer(Container container){}

    public ContainerVm getContainerVm(Container container){ return null; }

}
