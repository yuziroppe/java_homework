package model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserReader {
    private final String userName;
    public static int NAME_INDEX = 1;

    public UserReader(String userName) {
        this.userName = userName;
    }

    public static Object[][] getAllConcertsAsObject() {
        return getConcertsAsObject(getAllConcerts());
    }

    public Object[][] getMyConcertsAsObject() {
        return getConcertsAsObject(this.getMyConcerts());
    }

    public static Object[][] getConcertsAsObject(ArrayList<ArrayList<String>> concerts) {
        Object[][] retConcerts = new Object[concerts.size()][7];

        for (int i = 0; i < concerts.size(); i++) {
            for (int j = 0; j < concerts.get(i).size(); j++) {
                if (j == 0) {
                    retConcerts[i][j] = concerts.get(i).get(j).equals("true");
                } else {
                    retConcerts[i][j] = concerts.get(i).get(j);
                }
            }
        }
        return retConcerts;
    }

    public Object[][] getShowConcertsAsObject() {
        var allConcerts = getAllConcerts();
        var myConcerts = this.getMyConcerts();

        ArrayList<ArrayList<String>> retConcertsObject = new ArrayList<>();
        int index = 0;
        for (var concert : allConcerts) {
            ArrayList<String> retConcert = concert;
            for (var myConcert : myConcerts) {
                boolean sameFlag = true;
                for (int i = 1; i < 7; i++) {
                    if (!concert.get(i).equals(myConcert.get(i))) {
                        sameFlag = false;
                        break;
                    }
                }
                if (sameFlag && myConcert.get(0).equals("true")) {
                    retConcert = myConcert;
                    break;
                }
            }

            retConcertsObject.add(retConcert);
        }

        return getConcertsAsObject(retConcertsObject);
    }

    public String getUserName() {
        return this.userName;
    }

    private String getUserConcertsFilePath() {
        return System.getProperty("user.dir") + "/model/data/user_concerts/" + "concerts_" + this.userName + ".csv";
    }

    // based on the idea in which concerts name isn't duplicated
    public void updateMyConcerts(ArrayList<Integer> addedIndexList, ArrayList<String> deletedNameList) {
        ArrayList<ArrayList<String>> myConcerts = getMyConcerts();

        for (var index : addedIndexList) {
            ArrayList<String> concert = getConcertByIndex(index);

            concert.set(0, "true");

            boolean existFlag = true;

            for (var myConcert : myConcerts) {
                if (myConcert.equals(concert)) {
                    existFlag = false;
                    break;
                }
            }

            if (existFlag) {
                myConcerts.add(concert);
            }
        }

        for (var deletedConcertName : deletedNameList) {
            for (int i=0;i<myConcerts.size();i++) {
                if (myConcerts.get(i).get(1).equals(deletedConcertName)) {
                    myConcerts.remove(i);
                    break;
                }
            }
        }

        writeMyConcerts(myConcerts);
    }

    public void writeMyConcerts(ArrayList<ArrayList<String>> mc) {
        BufferedWriter myConcertBuffer = null;

        try {
            myConcertBuffer = Files.newBufferedWriter(Paths.get(getUserConcertsFilePath()), Charset.defaultCharset());
        } catch (IOException e) {
            System.exit(1);
        }

        for (var concert : mc) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < concert.size(); i++) {
                s.append(concert.get(i));
                if (i != concert.size() - 1) {
                    s.append(",");
                }
            }

            try {
                myConcertBuffer.write(s.toString());
                myConcertBuffer.newLine();
            } catch (IOException e) {
                System.exit(1);
            }
        }

        try {
            myConcertBuffer.close();
        } catch (IOException e) {
            System.exit(1);
        }
    }

    public ArrayList<ArrayList<String>> getMyConcerts() {
        String fname = getUserConcertsFilePath();
        File f = new File(fname);

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed to read a file");
                System.exit(1);
            }

            return new ArrayList<>();
        }

        BufferedReader fbr = null;

        try {
            fbr = new BufferedReader(new FileReader(f));
        } catch (IOException e) {
            System.exit(1);
        }

        ArrayList<ArrayList<String>> myConcerts = new ArrayList<ArrayList<String>>();

        while (true) {
            String tmpLine = null;

            try {
                tmpLine = fbr.readLine();
            } catch (IOException e) {
                System.exit(1);
            }

            if (tmpLine == null) break;

            String[] tmp = tmpLine.split(",", 0);
            ArrayList<String> concert = new ArrayList<>(Arrays.asList(tmp));
            myConcerts.add(concert);
        }

        try {
            fbr.close();
        } catch (IOException e) {
            System.exit(1);
        }

        return myConcerts;
    }

    public void deleteMyConcert(int index) {
        ArrayList<ArrayList<String>> mc = getMyConcerts();
        mc.remove(index);

        writeMyConcerts(mc);
    }

    private static String getUserConcertsFilePath(String userName) {
        return System.getProperty("user.dir") + "/model/data/user_concerts/" + "concerts_" + userName + ".csv";
    }

    private static String getFileNamePath(String fileName) {
        return System.getProperty("user.dir") + "/model/data/" + fileName;
    }

    public static boolean isCorrectUser(String userName, String password) {
        ArrayList<ArrayList<String>> usersList = getAllUsers();

        for (var up : usersList) {
            if (userName.equals(up.get(0)) && password.equals(up.get(1))) {
                return true;
            }
        }

        return false;
    }

    public static ArrayList<ArrayList<String>> getAllUsers() {
        List<String> rows = null;

        try {
            rows = Files.readAllLines(Paths.get(getFileNamePath("users.csv")), Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("Failed to read a file");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> userslist = new ArrayList<>();

        for (String row : rows) {
            ArrayList<String> up = new ArrayList<>();
            Collections.addAll(up, row.split(",", 0));
            userslist.add(up);
        }

        return userslist;
    }

    public static ArrayList<ArrayList<String>> getAllConcerts() {
        List<String> rows = null;

        try {
            rows = Files.readAllLines(Paths.get(getFileNamePath("concerts.csv")), Charset.defaultCharset());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("Failed to read a file");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> concertslist = new ArrayList<>();

        for (String row : rows) {
            ArrayList<String> c = new ArrayList<>();
            Collections.addAll(c, row.split(",", 0));
            concertslist.add(c);
        }

        return concertslist;
    }

    public static ArrayList<ArrayList<String>> getUserConcerts(String userName) {
        List<String> rows = null;

        try {
            String fname = getUserConcertsFilePath(userName);
            File f = new File(fname);

            if (!f.exists()) {
                return new ArrayList<>();
            }

            rows = Files.readAllLines(Paths.get(fname), Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println("Failed to read a file");
            System.exit(1);
        }

        ArrayList<ArrayList<String>> concertslist = new ArrayList<>();

        for (String row : rows) {
            ArrayList<String> c = new ArrayList<>();
            Collections.addAll(c, row.split(",", 0));
            concertslist.add(c);
        }

        return concertslist;
    }


    // Search for specific subscribers with a concert name
    public static ArrayList<ArrayList<String>> getSpecificConcerts(String concertName) {
        ArrayList<ArrayList<String>> allUsers = UserReader.getAllUsers();

        ArrayList<ArrayList<String>> retUsers = new ArrayList<>();

        for (ArrayList<String> user : allUsers) {
            String uname = user.get(NAME_INDEX);

            var concerts = getUserConcerts(uname);

            for (ArrayList<String> concert : concerts) {
                if (concertName.equals(concert.get(NAME_INDEX))) {
                    retUsers.add(user);
                    break;
                }
            }
        }

        return retUsers;
    }

    // search for concert name with prefix
    public static ArrayList<ArrayList<String>> searchForConcerts(String prefix) {
        ArrayList<ArrayList<String>> allConcerts = getAllConcerts();

        String regex = "^" + prefix;
        Pattern p = Pattern.compile(regex);

        ArrayList<ArrayList<String>> retConcerts = new ArrayList<>();

        for (var concert : allConcerts) {
            Matcher m = p.matcher(concert.get(NAME_INDEX));

            if (m.find()) {
                retConcerts.add(concert);
            }
        }

        return retConcerts;
    }

    public static ArrayList<String> getConcertByIndex(int index) {
        var allConcerts = getAllConcerts();
        for (int i = 0; i < allConcerts.size(); i++) {
            if (index == i) {
                return allConcerts.get(i);
            }
        }
        return new ArrayList<String>();
    }
}
