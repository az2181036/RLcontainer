package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.container.core.Container;
import org.cloudbus.cloudsim.container.core.ContainerVm;
import org.cloudbus.cloudsim.container.lists.ContainerVmList;
import org.cloudbus.cloudsim.container.resourceAllocators.PowerContainerAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class PowerContainerAllocationPolicyRL extends PowerContainerAllocationPolicy {

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
        setSimulationRam(new ArrayList<>());
    }

    @Override
    public ContainerVm findVmForContainer(Container container) {
        setSimulationRam();
        setSimulationCPU();
        List <Integer> S = computeS((DeadlineContainer) container);

        if(getQ().containsKey(S)){
            double[] q = getQ().get(S);
            return ContainerVmList.getById(getContainerVmList(),getMaxIdx(q)+1);
        }
        return findVmForContainerKeyHasNoS(container);
    }

    public ContainerVm findVmForContainerKeyHasNoS(Container container){
        Log.printLine("Q Table doesn't have the S, round searching a Vm.");
        for (ContainerVm containerVm : getContainerVmList()) {
//                Log.printConcatLine("Trying vm #",containerVm.getId(),"For container #", container.getId());
            if (containerVm.isSuitableForContainer(container)) {
                return containerVm;
            }
        }
        return null;
    }

    //
    public void deallocateVmForContainer(DeadlineContainer container) {
        ContainerVm containerVm = getContainerTable().remove(container.getUid());
        if (containerVm != null) {
            containerVm.containerDestroy(container);
        }
    }

    public ContainerVm getContainerVm(DeadlineContainer container) {
        return getContainerTable().get(container.getUid());
    }

    public ContainerVm getContainerVm(int containerId, int userId) {
        return getContainerTable().get(DeadlineContainer.getUid(userId, containerId));
    }

    public void RLTrain(List<ContainerVm> vmList){
        setContainerVmList(vmList);
        double delta = (getEpsilon() - getFinalEpsilon()) / (getNumberEpoch() * getEpsilonRate());
        for(int epoch=0;epoch<getNumberEpoch();epoch++){
            setSimulationRam();
            setSimulationCPU();
            List <Integer> S = computeS(getContainerList().get(0));
            for(int i=0;i<getContainerList().size();i++){
                DeadlineContainer container = getContainerList().get(i);

                int action;
                double[] q = new double[vmList.size()];
                if(getQ().containsKey(S)) q = getQ().get(S);

                if (getRandom().nextDouble() < getEpsilon()) {
                    action = getRandom().nextInt(vmList.size());
                }
                else action = getMaxIdx(q);

                double reward = computeReward(action, S);
                updateSimulationCPU(action, container);
                updateSimulationRam(action, container);

                List <Integer> _S;
                double[] _q = new double[vmList.size()];
                if(i < getContainerList().size()-1){
                    _S = computeS(getContainerList().get(i+1));
                    if(getQ().containsKey(_S)) _q = getQ().get(_S);
                    S = _S;
                }
                computeQ(q, action, reward, _q);
                putQ(S, q);
            }
            if(getEpsilon()>getFinalEpsilon()) setEpsilon(getEpsilon() - delta);
        }
    }

    public int getMaxIdx(double[] arr){
        if(arr==null||arr.length==0)
            return -1;
        int idx=0;
        for(int i =0;i<arr.length-1;i++){
            if(arr[idx]>arr[i+1]){
                idx = i;
            }
        }
        return idx + 1;
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
        ContainerVm containerVm = getContainerVmList().get(i);
        if (containerVm.isSuitableForContainer(container))
            if (container.getDeadline() < 0 ||
                    containerVm.updateContainersProcessing(CloudSim.clock())+ container.getEstimateTimeToFinish() <= container.getDeadline())
                multiplicator = 1;
        double tmp = Math.max(Math.floor(getSimulationCPU().get(i)/container.getCurrentRequestedTotalMips()),
                Math.floor(getSimulationRam().get(i)/ container.getRam()));
        return (int)(multiplicator * tmp);
    }

    public List<Integer> computeS(DeadlineContainer container){
        ArrayList<Integer> S = new ArrayList<>();
        for (int i=0;i<getContainerVmList().size();i++)
            S.add(computeSValue(i, container));
        return S;
    }

    public List<Double> getSimulationCPU(){
        return this.simulationCPU;
    }

    public void setSimulationCPU(){
        getSimulationCPU().clear();
        for (ContainerVm containerVm : getContainerVmList()) {
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
        getSimulationRam().clear();
        for (ContainerVm containerVm : getContainerVmList()) {
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

    public List<Map<String, Object>> optimizeAllocation(List<? extends Container> containerList) { return null; }
}
