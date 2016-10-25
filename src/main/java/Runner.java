import java.io.FileNotFoundException;

/**
 * @author: Bauka23
 * @date: 20.10.2016
 */
public class Runner {
    public static void main(String[] args) {
        int[] resources, consumers;
        int[][] tariffMatrix;
        try {
            resources = buildData(FileWorker.read("resources.txt"));
            consumers = buildData(FileWorker.read("consumers.txt"));
            tariffMatrix = buildTariffMatrix(FileWorker.read("tariffs.txt"), resources.length, consumers.length);
        } catch (FileNotFoundException e) {
            resources = new int[0];
            consumers = new int[0];
            tariffMatrix = new int[0][0];
            e.printStackTrace();
        }
        buildResult(resources, consumers, tariffMatrix);
    }

    private static int[] buildData(String line) {
        String[] arr = line.split("\n");
        String[] sData = arr[0].split(" ");
        int[] data = new int[sData.length];
        for (int i = 0; i < sData.length; i++) {
            data[i] = Integer.parseInt(sData[i]);
        }
        return data;
    }


    private static int[][] buildTariffMatrix(String sMatrix, int height, int weight) {
        int[][] matrix = new int[height][weight];
        String[] lines = sMatrix.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[0].length() > 0) {
                matrix[i] = buildData(lines[i]);
            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < weight; j++) {
                System.out.print(" " + matrix[i][j]);
            }
            System.out.println();
        }
        return matrix;
    }

    private static void buildResult(int[] resources, int[] consumers, int[][] tariffMatrix) {
        int[][] result = buildPlan(resources, consumers);
        Potential potential = getPotential(result, tariffMatrix, resources.length, consumers.length);
        if (potential.mark < 0) {
            result[potential.x][potential.y] = getMinimumValueFromResult(result, tariffMatrix, resources.length, consumers.length);
            System.out.println(result[potential.x][potential.y]);
        }
    }

    private static int[][] buildPlan(int[] resources, int[] consumers) {
        int[][] result = new int[resources.length][consumers.length];
        int[] bufferResources = cloneArray(resources);
        int[] bufferConsumers = cloneArray(consumers);
        int i = 0, j = 0;
        while (i < resources.length && j < consumers.length) {
            result[i][j] = getMinimum(bufferResources[i], bufferConsumers[j]);
            bufferResources[i] -= result[i][j];
            bufferConsumers[j] -= result[i][j];
            if (bufferResources[i] == 0) {
                i++;
            } else {
                j++;
            }
        }
        return result;
    }

    private static int[] cloneArray(int[] array) {
        int[] clone = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            clone[i] = array[i];
        }
        return clone;
    }

    private static int getMinimum(int... values) {
        int min = values[0];
        for (int value : values) {
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    private static Potential getPotential(int[][] result, int[][] tariffMatrix, int height, int weight) {
        int[] u = new int[height];
        boolean[] uCheck = new boolean[height];
        int[] v = new int[weight];
        boolean[] vCheck = new boolean[weight];

        u[0] = 0; uCheck[0] = true;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < weight; j++) {
                if (result[i][j] > 0) {
                    if (uCheck[i] && !vCheck[j]) {
                        System.out.println(tariffMatrix[i][j] - u[i]);
                        v[j] = tariffMatrix[i][j] - u[i];
                        vCheck[j] = true;
                    } else if (vCheck[j] && !uCheck[i]) {
                        u[i] = tariffMatrix[i][j] - v[j];
                        uCheck[j] = true;
                    }
                }
            }
        }

        Potential potential = new Potential();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < weight; j++) {
                if (result[i][j] == 0) {
                    if (!potential.initialized) {
                        potential.mark = tariffMatrix[i][j] - (u[i] + v[j]);
                        potential.x = i;
                        potential.y = j;
                        potential.initialized = true;
                    } else {
                        int tempMark = tariffMatrix[i][j] - (u[i] + v[j]);
                        if (tempMark < potential.mark) {
                            potential.mark = tempMark;
                            potential.x = i;
                            potential.y = j;
                        }
                    }
                }
            }
        }
        return potential;
    }

    private static int getMinimumValueFromResult(int[][] result, int[][] tariffMatrix, int height, int weight) {
        int[] variants = new int[height];
        for (int i = 0; i < height; i++) {
            int k = 0;
            for (int j = 0; j < weight; j++) {
                if (result[i][j] > 0) {
                    k = j;
                    break;
                }
            }
            for (int j = 0; j < weight; j++) {
                if (result[i][j] > 0 && tariffMatrix[i][j] > tariffMatrix[i][k]) {
                    k = j;
                }
            }
            variants[i] = result[i][k];
        }
        return getMinimum(variants);
    }
}
