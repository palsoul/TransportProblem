import java.io.FileNotFoundException;

/**
 * @author: Bauka23
 * @date: 20.10.2016
 */
public class Runner {
    public static void main(String[] args) {
        int[] resources, demand;
        int[][] tariffMatrix;
        try {
            resources = buildData(FileWorker.read("resources.txt"));
            demand = buildData(FileWorker.read("demand.txt"));
            tariffMatrix = buildTariffMatrix(FileWorker.read("tariffs.txt"));
        } catch (FileNotFoundException e) {
            resources = new int[0];
            demand = new int[0];
            tariffMatrix = new int[0][0];
            e.printStackTrace();
        }
        buildResult(resources, demand, tariffMatrix);
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


    private static int[][] buildTariffMatrix(String sMatrix) {
        String[] lines = sMatrix.split("\n");
        int[][] matrix = new int[lines.length][lines[0].split(" ").length];
        for (int i = 0; i < lines.length; i++) {
            if (lines[0].length() > 0) {
                matrix[i] = buildData(lines[i]);
            }
        }
        return matrix;
    }

    private static void buildResult(int[] resources, int[] demand, int[][] tariffMatrix) {
        int[][] result = buildPlan(resources, demand);
        Potential potential = findPotential(result, tariffMatrix);
        Mark mark = findMinimumMark(result, tariffMatrix, potential);
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

    private static Potential findPotential(int[][] result, int[][] tariffMatrix) {
        int height = result.length, width = result[0].length;
        Potential potential = new Potential(new int[height], new int[width]);
        boolean[] uCheck = new boolean[height]; // показывает, проинициализирован ли элемент
        boolean[] vCheck = new boolean[width]; // показывает, проинициализирован ли элемент

        potential.u[0] = 0;       int count = 1;      uCheck[0] = true;

        while (count < height + width) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (result[i][j] > 0) {
                        if (uCheck[i]) {
                            if (!vCheck[j]) {
                                potential.v[j] = tariffMatrix[i][j] - potential.u[i];
                                vCheck[j] = true;
                                count++;
                            }
                        } else if (vCheck[j]) {
                            if (!uCheck[j]) {
                                potential.u[i] = tariffMatrix[i][j] - potential.v[j];
                                uCheck[i] = true;
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return potential;
    }

    private static Mark findMinimumMark(int[][] result, int[][] tariffMatrix, Potential potential) {
        Mark minMark = new Mark(0, 0, 0);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                if (result[i][j] == 0) {
                    Mark mark = new Mark(i, j, tariffMatrix[i][j] - (potential.u[i] + potential.v[j]));
                    if (mark.isLessThan(minMark)) minMark = mark;
                }
            }
        }
        return minMark;
    }
}
