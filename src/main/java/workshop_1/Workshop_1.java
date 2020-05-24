package workshop_1;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static java.util.Calendar.*;

public class Workshop_1 {//corrected
    //stałe
    private static final String TASKS_CSV = "Tasks.csv";
    //wszystkie opcje
    private static final String[] OPTIONS = {"a - add", "r - remove", "l - list", "e - exit"};
    //opcje dla nowego pliku
    private static final String[] OPTIONS_N = {"a - add", "e - exit"};
    
    private static String[][] tasks;
    
    
    public static void main(String[] args) {
        startUp();//initial actions: file to array
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (tasks.length == 1) {//dla ograniczonych opcji
                while (true) {
                    if (input.matches("[ae]")) {//permitted
                        break;
                    } else {
                        System.out.println(ConsoleColors.RED_UNDERLINED + "Choose available option!" + ConsoleColors.RESET);
                        input = scanner.nextLine();
                    }
                }
            }
            switch (input) {
                case "e":
                    saveTasksToFile(TASKS_CSV, tasks);
                    System.out.println(ConsoleColors.RED + "File updated. Exiting.");
                    System.exit(0);
                    break;
                case "a":
                    addTask();
                    break;
                case "r":
                    deleteTask(tasks, delIndex());
                    //System.out.println("Value was successfully deleted.");
                    break;
                case "l":
                    displayAll(tasks);
                    System.out.println("\nReprinted\n");
                    break;
                default:
                    System.out.println("Please select a correct option.");
            }
            //startUp();
            if (tasks.length == 1) {
                displayOptions(OPTIONS_N);
            } else {
                displayAll(tasks);
                System.out.println();
                displayOptions(OPTIONS);
            }
        }//main loop
    }
    
    public static void startUp() {
        try {
            //czy plik istnieje i ma więcej linii niż tylko nagłówek
            if (Files.exists(Paths.get(TASKS_CSV)) && fileSize(TASKS_CSV) > 1) {
                System.out.println(ConsoleColors.BLUE + "Reading data from file " + TASKS_CSV + ConsoleColors.RESET);
                tasks = loadDataToTab(TASKS_CSV);
                displayAll(tasks);
                System.out.println();
                displayOptions(OPTIONS);//full opcja
            } else {
                System.out.println("File to be used: " + Paths.get(TASKS_CSV));
                //FILE_NAME.length();
                System.out.println(ConsoleColors.RED_UNDERLINED + "No existing file or file contains no data" + ConsoleColors.RESET);
                //createFile(FILE_NAME);//utwórz pusty plik
                newFileAndHeader(TASKS_CSV);//utwórz plik i wstaw nagłówki
                tasks = loadDataToTab(TASKS_CSV);
                displayOptions(OPTIONS_N);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void addTask() {
        Scanner scanner1 = new Scanner(System.in);
        System.out.println("Please add task description");
        String desc = scanner1.nextLine();
        System.out.println("Add task due date in \"dd-mm-yyyy\" format ");
        System.out.print("or number of days since today. ");
        String dueDat;
        dueDat = readDate(scanner1);
        System.out.println("Is your task is important: 1/0");
        String isImp = readImp(scanner1).toString();
        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        tasks[tasks.length - 1] = new String[3];
        tasks[tasks.length - 1][0] = desc;
        tasks[tasks.length - 1][1] = dueDat;
        tasks[tasks.length - 1][2] = isImp;
    }
    
    public static void deleteTask(String[][] tab, int index) {
        try {
            if (index < tab.length) {
                tasks = ArrayUtils.remove(tab, index);
                System.out.println("Row " + index + "is deleted.\n");
            } else {
                System.out.println(ConsoleColors.RED_UNDERLINED + "Enter correct index" + ConsoleColors.RESET);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }
    
    public static int delIndex() {
        Scanner scanner3 = new Scanner(System.in);
        System.out.println("Select index to delete.");
        
        String n = scanner3.nextLine();
        while (!isInexGreaterZero(n)) {
            System.out.println("Enter index > 0");
            n = scanner3.nextLine();
        }
        return Integer.parseInt(n);
    }
    
    public static boolean isInexGreaterZero(String input) {
        
        if (NumberUtils.isParsable(input)) {
            return Integer.parseInt(input) > 0;
            
        } else return false;
    }
    
    public static String readDate(Scanner sc) {
        System.out.print(ConsoleColors.GREEN);
        String inputDate;//if date entered
        Date parsedDate = null;
        int days;//days from today
        //Date newDate;
        int compareDates = 0;
        String wrongMsg = "Enter correct date in dd-mm-yyyy format";
        DateFormat dateF = new SimpleDateFormat("dd-MM-yyyy");
        Date todayObj = new Date();//dzisiejsza data
        String today = dateF.format(todayObj);//dzisiejsza data String dd-mm-yyyy
        System.out.println("Today is: " + today);
        //wprowadzenie daty - kontrola poprawności
        
        
        while (true) {
            inputDate = sc.nextLine();//SKANOWANIE
            if (inputDate.length() < 8) {
                days = Integer.parseInt(inputDate);
                inputDate = dateF.format(dateAdd(todayObj, days));
                //break;//podanie dni+today
            }
            try {
                parsedDate = dateF.parse(inputDate);
                compareDates = parsedDate.compareTo(todayObj);//porównanie dat w formacie Date
            } catch (ParseException ignored) {
            }
            
            //czy data z przyszłości
            if (isValidDate(inputDate)) {
                if (compareDates > 0) {
                    inputDate = dateF.format(parsedDate);
                    break;
                } else {
                    System.out.println("Please enter future date");
                }
            } else
                System.out.println(wrongMsg);
        }
        
        System.out.print(ConsoleColors.RESET);
        System.out.println("Your date: " + inputDate);
        return inputDate;
    }
    
    //adding days to date
    public static Date dateAdd(Date date, int days) {
        Calendar c = getInstance();
        c.setTime(date);
        c.add(DATE, days);
        date = c.getTime();
        return date;
    }
    
    public static void displayOptions(String[] tab) {
        System.out.println(ConsoleColors.BLUE + "Select an option: " + ConsoleColors.RESET);
        for (String option : tab) {
            System.out.println(option);
        }
    }
    
    //is it date in proper format
    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
    
    
    public static Character readImp(Scanner scan) {
        char imp;
        while (true) {
            imp = scan.next().charAt(0);//czyta tylko pierwszy znak
            if (imp == '0' || imp == '1') {
                break;
            }
            System.out.print("Enter 1 - important lub 0 -  not so imp.");
        }
        return imp;
    }
    
    public static void saveTasksToFile(String fileName, String[][] tab) {
        Path dir = Paths.get(fileName);
        
        String[] lines = new String[tasks.length];
        String task;
        for (int i = 0; i < tab.length; i++) {
            task = tab[i][0];
            
            //zamiana przecinka na 247
            tab[i][0] = task.replaceAll(",", Character.toString((char) 247));
            //System.out.println("Do pliku: "+tab[i][0]);
            lines[i] = String.join(",", tab[i]);
        }
        
        try {
            Files.write(dir, Arrays.asList(lines));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static String[][] loadDataToTab(String fileName) {
        Path dir = Paths.get(fileName);
        String task;
        if (!Files.exists(dir)) {
            System.out.println("No existing file or file is empty.");
            displayOptions(OPTIONS_N);
            System.exit(0);
        }
        String[][] tab = null;
        try {
            List<String> strings = Files.readAllLines(dir);
            if (strings.size() == 1) {//jedna linia w pliku
                System.out.println("File contains only header");
            }
            tab = new String[strings.size()][3];
            
            for (int i = 0; i < strings.size(); i++) {
                String[] split = strings.get(i).split(",");
                for (int j = 0; j < split.length; j++) {
                    task = split[j];
                    tab[i][j] = task.replaceAll(Character.toString((char) 247), ",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tab;
    }
    
    //wydruk zawartości
    public static void displayAll(String[][] tab) {
        int offSet = 33;
        int maxLen = maxTaskLen(tasks) + 2;
        if (maxLen < 11) maxLen = 11;
        String colFormat = " | %-2s";//index
        String colFormat1 = " | %-" + maxLen + "s";//task
        String colFormat2 = " | %-" + 10 + "s";//date, imp
        System.out.print(ConsoleColors.BLUE_BOLD);
        System.out.print(" |");//linia pozioma przed nagłówkiem
        System.out.print("-".repeat(maxLen + offSet));
        System.out.println("|");
        for (int i = 0; i < tab.length; i++) {
            System.out.format(colFormat, i);//szerokość dla indeksu
            for (int j = 0; j < tab[i].length; j++) {
                if (j == 0) {
                    System.out.format(colFormat1, tab[i][j]);//szerokość dla zadania
                } else {
                    System.out.format(colFormat2, tab[i][j]);//szerokość dla date, imp
                }
            }
            System.out.println(" |");
            //linia pozioma po nagłówku
            if (i == 0) {
                
                System.out.print(" |");
                System.out.print("-".repeat(maxLen + offSet));
                System.out.println("|");
                System.out.print(ConsoleColors.RESET);
                System.out.print(ConsoleColors.GREEN);
            }
        }
        //linia pozioma końcowa
        System.out.print(" |");
        System.out.print("-".repeat(maxLen + offSet));
        System.out.println("|");
        System.out.print(ConsoleColors.RESET);
    }
    
    
    public static int fileSize(String fileName) throws IOException {
        //File newFile = new File(fileName);
        Path dir = Paths.get(fileName);
        List<String> strings = Files.readAllLines(dir);
        return strings.size();
    }
    
    public static void newFileAndHeader(String fileName) {
        PrintWriter zapis = null;
        try {
            zapis = new PrintWriter(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert zapis != null;
        zapis.println("Task Name,Due Date,Importance");
        zapis.close();
    }
    
    public static int maxTaskLen(String[][] tab) {
        int max = 0;//max length of task descr.
        for (int i = 1; i < tab.length; i++) {
            if (tab[i][0].length() > max) max = tab[i][0].length();
        }
        return max;
    }
}
