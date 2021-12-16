package sorting;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    static String[] errorMsg = new String[]{
            "No sorting type defined!", "No data type defined!",
            " is not a valid parameter. It will be skipped.", " is not a long. It will be skipped."
    };
    static List<String> validTypes = List.of("byCount", "natural");
    static List<String> validDataTypes = List.of("long", "line", "word");
    static List<String> validArgs = List.of(
            "long", "line", "word", "byCount", "natural", "-sortingType", "-dataType", "-inputFile", "-outputFile"
    );
    static String outPath;

    public static void main(final String[] args) {
        List<String> params = Arrays.stream(args).collect(Collectors.toList());
        SortUtil.scanner = new Scanner(System.in);
        String sortingType = "default";
        if (params.contains("-sortingType")) {
            int index = params.indexOf("-sortingType");
            if (params.size() > index + 1 && validTypes.contains(params.get(index + 1))) {
                sortingType = params.get(index + 1);
            } else {
                err(0);
            }
        }

        String dataType = "default";
        if (params.contains("-dataType")) {
            int index = params.indexOf("-dataType");
            if (params.size() > index + 1 && validDataTypes.contains(params.get(index + 1))) {
                dataType = params.get(index + 1);
            } else {
                err(1);
            }
        }

        if (params.contains("-inputFile")) {
            int index = params.indexOf("-inputFile");
            if (params.size() > index + 1) {
                String inPath = params.get(index + 1);
                SortUtil.scanner = new Scanner(inPath);
            } else {
                System.out.println("???no in-path defined");
            }
        }

        if (params.contains("-outputFile")) {
            int index = params.indexOf("-outputFile");
            if (params.size() > index + 1) {
                outPath = params.get(index + 1);
                try {
                    FileWriter writer = new FileWriter(outPath);
                    writer.write("result..."); // not implemented
                    writer.close();
                } catch (Exception e) {
                    System.out.println("No Permissions");
                }
            } else {
                System.out.println("???no out-path defined");
            }
        }

        params.forEach(p -> {
            if (!validArgs.contains(p)) System.out.printf("%s" + errorMsg[2], p);
        });

        if ("byCount".equals(sortingType)) {
            switch (dataType) {
                case "long":
                    SortUtil.sortLong(false);
                    break;
                case "line":
                    SortUtil.sortLine(false);
                    break;
                default:
                    SortUtil.sortWord(false);
                    break;
            }
        } else {
            switch (dataType) {
                case "long":
                    SortUtil.sortLong(true);
                    break;
                case "line":
                    SortUtil.sortLine(true);
                    break;
                default:
                    SortUtil.sortWord(true);
                    break;
            }
        }
    }

    static void err(int i) {
        System.out.println(errorMsg[i]);
    }
}

class SortUtil {
    static Scanner scanner; // = new Scanner(System.in);

    public static void sortLong(boolean isSort) {
        List<Long> data = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String[] input = scanner.nextLine().split("\\s+");
            for (String str : input) {
                try {
                    long nextLong = Long.parseLong(str);
                    data.add(nextLong);
                } catch (Exception e) {
                    System.out.printf("%s" + Main.errorMsg[3], str);
                }
            }
        }
        if (data.isEmpty()) return;

        System.out.printf("Total numbers: %d.\n", data.size());
        if (isSort) {
            System.out.printf("Sorted data: %s\n", data.stream()
                    .sorted().map(Object::toString)
                    .collect(Collectors.joining(" ")));
        } else {
            Map<Long, Long> count = data.stream().collect(
                    Collectors.groupingBy(Function.identity(), Collectors.counting())
            );
            formatNumber(count, data.size());
        }
    }

    public static void sortLine(boolean isSort) {
        List<String> data = new ArrayList<>();
        while (scanner.hasNextLine()) data.add(scanner.nextLine().trim());
        if (data.isEmpty()) return;

        System.out.printf("Total lines: %d.\n", data.size());
        if (isSort) {
            System.out.println("Sorted data:");
            data.stream().sorted().forEachOrdered(System.out::println);
        } else {
            Map<String, Long> count = data.stream().collect(
                    Collectors.groupingBy(Function.identity(), Collectors.counting())
            );
            SortUtil.formatString(count, data.size());
        }
    }

    public static void sortWord(boolean isSort) {
        List<String> data = new ArrayList<>();
        while (scanner.hasNextLine()) data.addAll(List.of(scanner.nextLine().trim().split("\\s+")));
        if (data.isEmpty()) return;

        System.out.printf("Total words: %d.\n", data.size());
        if (isSort) {
            System.out.printf("Sorted data: %s\n", data.stream()
                    .sorted()
                    .collect(Collectors.joining(" ")));
        } else {
            Map<String, Long> count = data.stream().collect(
                    Collectors.groupingBy(Function.identity(), Collectors.counting())
            );
            SortUtil.formatString(count, data.size());
        }
    }

    static void formatString(Map<String, Long> count, int total) {
        PriorityQueue<Data> queue =
                new PriorityQueue<>(Comparator.comparing(Data::getCount).thenComparing(Data::getPresent));
        count.forEach((k, v) -> queue.add(new Data(k, v)));
        while (!queue.isEmpty()) {
            Data data = queue.poll();
            System.out.printf("%s: %d time(s), %.0f%%.\n", data.present, data.count, (data.count * 1.0 / total) * 100);
        }
    }

    static void formatNumber(Map<Long, Long> count, int total) {
        PriorityQueue<Data> queue =
                new PriorityQueue<>(Comparator.comparing(Data::getCount).thenComparing(Data::getValue));
        count.forEach((k, v) -> queue.add(new Data(k, v)));
        while (!queue.isEmpty()) {
            Data data = queue.poll();
            System.out.printf("%d: %d time(s), %.0f%%.\n", data.value, data.count, (data.count * 1.0 / total) * 100);
        }
    }
}

class Data {
    String present;
    Long value;
    Long count;

    Data(Long value, Long count) {
        this.value = value;
        this.count = count;
    }

    Data(String present, Long count) {
        this.present = present;
        this.count = count;
    }

    public String getPresent() {
        return present;
    }

    public Long getValue() {
        return value;
    }

    public Long getCount() {
        return count;
    }
}
