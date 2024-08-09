package localapp.service;

import localapp.config.LottoType;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileReaderServiceKenoImpl extends FileReaderService {

    private static final String SEPARATOR = ";";
    private static final int FIRST_INDEX = 4;


    public FileReaderServiceKenoImpl(LottoType type) {
        super(type);
    }


    @Override
    public List<int[]> getAllNumbers() {
        List<int[]> results = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(super.url)))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] data = line.split(SEPARATOR);
                int[] numbersInCurrentRow = new int[super.drawnedNumber];
                for (int index = FIRST_INDEX; index < data.length; index++) {
                    numbersInCurrentRow[index - FIRST_INDEX] = Integer.parseInt(data[index]);
                }
                results.add(numbersInCurrentRow);
            }
        } catch (IOException exp) {
            throw new RuntimeException(exp.getMessage());
        }

        return results;
    }

}