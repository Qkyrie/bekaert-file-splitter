package com.deswaef.bekaert;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class BekaertApplication implements CommandLineRunner {

    public static final String INPUT_FOLDER = "input";
    public static final String OUTPUT_BASE = "output";

    @Value("${splitCount}")
    private int split;


    private void splitFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));

        List<String> allLines = br.lines()
                .collect(Collectors.toList());

        String firstLine = allLines.get(0);
        allLines.remove(0);


        List<List<String>> chopped = chopped(allLines, split);

        chopped
                .forEach(rowList -> {
                    try {
                        FileWriter fileWriter = new FileWriter(new File(OUTPUT_BASE + File.separator + file.getName() + "-" + chopped.indexOf(rowList) + ".csv"));
                        fileWriter.write(firstLine + "\n");
                        rowList
                                .stream()
                                .forEach(row -> {
                                    try {
                                        fileWriter.write(row + "\n");
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        System.out.println("something went wronggg :( :( :(");
                                    }
                                });
                        fileWriter.close();
                    } catch (Exception ex) {
                        System.out.println("Something went wrong... :(");
                        ex.printStackTrace();
                    }
                });
    }

    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    @Override
    public void run(String... args) throws Exception {
        preRequisites();
        File inputdirectory = new File(INPUT_FOLDER);

        File[] files = inputdirectory.listFiles();
        for (File file : files) {
            if (file.getName().endsWith("xlsx") || file.getName().endsWith("csv")) {
                splitFile(file);
            }
        }

    }

    private void preRequisites() {
        File file = new File(OUTPUT_BASE);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BekaertApplication.class, args);
    }
}
