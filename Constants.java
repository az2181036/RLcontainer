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
    public static final double SCHEDULING_INTERVAL = 5000.0D;
    public static final double SIMULATION_LIMIT = 87400.0D;
    public static final double CONTAINER_STARTTUP_DELAY = 0.4;//the amount is in seconds
    public static final double VM_STARTTUP_DELAY = 100;//the amoun is in seconds

    public static final int NUMBER_HOSTS = 20;
    public static final int NUMBER_VMS = 20;
    public static final int NUMBER_CLOUDLETS = 50;

    public static final int HOST_TYPES = 3;
    public static final int[] HOST_MIPS = new int[]{37274, 37274, 37274};
    public static final int[] HOST_PES = new int[]{4, 8, 16};
    public static final int[] HOST_RAM = new int[]{2500, 5000, 10000};
    public static final int HOST_BW = 0;
    public static final PowerModel[] HOST_POWER = new PowerModel[]{new PowerModelSpecPowerHpProLiantMl110G4Xeon3040(),
            new PowerModelSpecPowerHpProLiantMl110G5Xeon3075(), new PowerModelSpecPowerIbmX3550XeonX5670()};

    public static final int VM_TYPES = 3;
    public static final double[] VM_MIPS = new double[]{37274, 37274, 37274};
    public static final int[] VM_PES = new int[]{4, 8, 16};
    public static final float[] VM_RAM = new float[] {2500, 5000, 10000};//**MB*
    public static final int VM_BW = 0;
    public static final int VM_SIZE = 2500;

    public static final int CONTAINER_TYPES = 1;
    public static final int[] CONTAINER_MIPS = new int[]{500};
    public static final int[] CONTAINER_PES = new int[]{1};
    public static final int[] CONTAINER_RAM = new int[]{25};
    public static final int CONTAINER_BW = 0;

    public static final long CLOUDLET_LENGTH = 8000000 * 5;
    public static final int CLOUDLET_PES = 1;
}
