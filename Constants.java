package org.cloudbus.cloudsim.examples.RLcontainer;

import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G5Xeon3075;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerIbmX3550XeonX5670;

public class Constants {
    public static final int SEED = 500;
    public static final int NUMBEREPOCH = 500;
    public static final double GAMMA = 0.9;
    public static final double EPSILON = 1;
    public static final double EPSILONRATE = 0.5;
    public static final double FINALEPSILON = 0.1;

    public static final boolean ENABLE_OUTPUT = true;
    public static final boolean OUTPUT_CSV = false;
    public static final double SCHEDULING_INTERVAL = 300.0D;
    public static final double SIMULATION_LIMIT = 87400.0D;
    public static final double CONTAINER_STARTTUP_DELAY = 0.4;//the amount is in seconds
    public static final double VM_STARTTUP_DELAY = 100;//the amoun is in seconds

    public static final int NUMBER_HOSTS = 20;
    public static final int NUMBER_VMS = 20;
    public static final int NUMBER_CLOUDLETS = 50;

    public static final int HOST_TYPES = 1;
    public static final int[] HOST_MIPS = new int[]{2500};
    public static final int[] HOST_PES = new int[]{96};
    public static final int[] HOST_RAM = new int[]{100};
    public static final int HOST_BW = 0;
    public static final PowerModel[] HOST_POWER = new PowerModel[]{new PowerModelSpecPowerHpProLiantMl110G4Xeon3040()};

    public static final int VM_TYPES = 1;
    public static final double[] VM_MIPS = new double[]{2500};
    public static final int[] VM_PES = new int[]{96};
    public static final float[] VM_RAM = new float[] {100};//**MB*
    public static final int VM_BW = 0;
    public static final int VM_SIZE = 2500;

    public static final int CONTAINER_TYPES = 3;
    public static final int[] CONTAINER_MIPS = new int[]{100};
    public static final int[] CONTAINER_PES = new int[]{1};
    public static final int[] CONTAINER_RAM = new int[]{50};
    public static final int CONTAINER_BW = 0;

    public static final long CLOUDLET_LENGTH = 8550000;
    public static final int CLOUDLET_PES = 1;
}
