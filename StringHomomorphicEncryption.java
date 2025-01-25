import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StringHomomorphicEncryption {

    private static final List<Integer> qi = new ArrayList<>();
    private static final List<Integer> ri = new ArrayList<>();
    private static int secretKey = 7;

    public static void generatePublicKeys(int numKeys) {
        for (int i = 0; i < numKeys; i++) {
            qi.add((i + 1) * 3);
            ri.add((i + 1) * 2);
        }
        System.out.println("Public Key qi: " + qi);
        System.out.println("Public Key ri: " + ri);
    }

    public static String encryptCharacter(char m, int r) {
        int asciiValue = (int) m;
        int subsetSum = 0;
        for (int i = 0; i < qi.size(); i++) {
            subsetSum += secretKey * qi.get(i) + 2 * ri.get(i);
        }
        int c = asciiValue + 2 * r + 2 * subsetSum;
        return String.format("%05d", c);
    }

    public static List<String> encryptString(String s) {
        List<String> ciphertexts = new ArrayList<>();
        int r = 10;
        for (char ch : s.toCharArray()) {
            ciphertexts.add(encryptCharacter(ch, r));
            r += 5;
        }
        return ciphertexts;
    }

    public static String combineCiphertexts(List<String> c1List, List<String> c2List) {
        StringBuilder combinedResults = new StringBuilder();
        StringBuilder sumPart = new StringBuilder();
        StringBuilder diffPart = new StringBuilder();
        int l1 = c1List.size();
        int l2 = c2List.size();
        String lengthSum = String.format("%05d", l1 + l2);
        String lengthDiff = String.format("%04d", Math.abs(l1 - l2));
        lengthDiff = (l1 >= l2 ? "0" : "1") + lengthDiff;

        combinedResults.append(lengthSum).append(lengthDiff);
        int maxLength = Math.max(l1, l2);
        for (int i = 0; i < maxLength; i++) {
            int c1 = (i < c1List.size()) ? Integer.parseInt(c1List.get(i)) : 0;
            int c2 = (i < c2List.size()) ? Integer.parseInt(c2List.get(i)) : 0;
            String sum = String.format("%05d", c1 + c2);
            sumPart.append(sum);
            if (i < Math.min(c1List.size(), c2List.size())) {
                int difference = c1 - c2;
                String diffSign = (difference < 0 ? "1" : "0");
                String diff = String.format("%04d", Math.abs(difference));
                diffPart.append(diffSign).append(diff);
            }
        }
        combinedResults.append(sumPart).append(diffPart);

        return combinedResults.toString();
    }

    public static char decryptCharacter(String c, int r) {
        int encryptedValue = Integer.parseInt(c);
        int subsetSum = 0;
        for (int i = 0; i < qi.size(); i++) {
            subsetSum += secretKey * qi.get(i) + 2 * ri.get(i);
        }
        int asciiValue = (encryptedValue - 2 * r - 2 * subsetSum);
        return (char) asciiValue;
    }

    public static String decryptCombinedCiphertext(String combinedCiphertext) {
        int currentIndex = 0;
        int l1PlusL2 = Integer.parseInt(combinedCiphertext.substring(currentIndex, currentIndex + 5));
        currentIndex += 5;

        String l1MinusL2WithSign = combinedCiphertext.substring(currentIndex, currentIndex + 5);
        int sign = l1MinusL2WithSign.charAt(0) == '1' ? -1 : 1;
        int l1MinusL2 = Integer.parseInt(l1MinusL2WithSign.substring(1)) * sign;
        currentIndex += 5;

        int l1 = (l1PlusL2 + l1MinusL2) / 2;
        int l2 = l1PlusL2 - l1;

        int sumLength= Math.max(l1,l2);
        String sumPart = combinedCiphertext.substring(currentIndex, currentIndex + (5 * sumLength));
        currentIndex += (5 * sumLength); 

        String diffPart = combinedCiphertext.substring(currentIndex);
        StringBuilder s = new StringBuilder();
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        int r = 10;
        List<String> x = new ArrayList<>();
        List<String> y = new ArrayList<>();
        for (int i = 0; i < sumLength; i++) {
            String c1 = sumPart.substring(i * 5, (i + 1) * 5);
            x.add(c1);
        }
        for (int i = 0; i < Math.min(l1, l2); i++) {
            String c1 = diffPart.substring(i * 5, (i + 1) * 5);
            y.add(c1);
        }

        r = 10;
        for (int i = 0; i < Math.min(l1, l2); i++) {
            int x1 = Integer.parseInt(x.get(i));
            String c = y.get(i);
            int diffSign = (c.charAt(0) == '1') ? -1 : 1;
            int x2 = Integer.parseInt(c.substring(1)) * diffSign;
            int c1= (x1 + x2)/2;
            int c2 = x1-c1;
            String c1s = String.valueOf(c1);
            String c2s = String.valueOf(c2);
            s1.append(decryptCharacter(c1s, r));
            s2.append(decryptCharacter(c2s, r));
            r+=5;
        }

        for (int i = Math.min(l1, l2) ; i < Math.max(l1,l2) ; i++) {
            String x1 = x.get(i);
            if(l1>l2){
            s1.append(decryptCharacter(x1, r));
            }
            else{
            s2.append(decryptCharacter(x1, r));
            }
            r+=5;
        }
        s.append(s1.toString()).append(s2.toString());
        return s.toString();
    }

    public static void main(String[] args) {
        generatePublicKeys(5);
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the String: ");
        String s1 = scanner.nextLine();
        String s2 = "";
        
        List<String> c1List = encryptString(s1);
        List<String> c2List = encryptString(s2);
        
        String combinedResults = combineCiphertexts(c1List, c2List);
        System.out.println("Ciphertext: " + combinedResults);
        
        while (true) {
            System.out.println("\n1. Append the Text");
            System.out.println("2. Modify the entire Text");
            System.out.println("3. Modify the parlicular word or letter in  Text");
            System.out.println("4. Decrypt the Text");
            System.out.println("5. Exit");
            System.out.print("Enter your Choice: ");
            int ch = scanner.nextInt();
            scanner.nextLine(); 
            
            switch (ch) {
                case 1:
                    System.out.print("Enter the String to append: ");
                    String newText = scanner.nextLine();
                    s2 =s2 +" " + newText;
                    c2List = encryptString(s2);
                    combinedResults = combineCiphertexts(c1List, c2List);
                    System.out.println("Updated Ciphertext: " + combinedResults);
                    break;
            
                case 2:
                    System.out.println("Which string do you want to modify?");
                    System.out.println("1. Modify s1");
                    System.out.println("2. Modify s2");
                    System.out.print("Enter your choice: ");
                    int modifyChoice = scanner.nextInt();
                    scanner.nextLine(); 
                
                    switch (modifyChoice) {
                        case 1:
                            System.out.print("Enter the Modified String for s1: ");
                            s1 = scanner.nextLine();
                            c1List = encryptString(s1);
                            break;
                        case 2:
                            System.out.print("Enter the Modified String for s2: ");
                            s2 = scanner.nextLine();
                            c2List = encryptString(s2);
                            break;
                        default:
                            System.out.println("Invalid choice. No modifications made.");
                            break;
                    }
                    combinedResults = combineCiphertexts(c1List, c2List);
                    System.out.println("Updated Ciphertext: " + combinedResults);
                    break;
                case 3:
                    System.out.println("Which string do you want to modify?");
                    System.out.println("1. Modify s1");
                    System.out.println("2. Modify s2");
                    System.out.print("Enter your choice: ");
                    int mChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                
                    switch (mChoice) {
                        case 1:
                            System.out.println("Current String (s1): " + s1);
                            System.out.println("1. Modify a particular letter");
                            System.out.println("2. Modify a word");
                            System.out.print("Enter your choice: ");
                            int modifyType1 = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                
                            if (modifyType1 == 1) {
                                System.out.print("Enter the position of the letter to modify (1-based index): ");
                                int position = scanner.nextInt();
                                scanner.nextLine(); // Consume the newline character
                                if (position <= 0 || position > s1.length()) {
                                    System.out.println("Invalid position.");
                                    break;
                                }
                                System.out.print("Enter the new letter: ");
                                char newLetter = scanner.nextLine().charAt(0);
                                s1 = s1.substring(0, position - 1) + newLetter + s1.substring(position);
                            } else if (modifyType1 == 2) {
                                System.out.print("Enter the word to replace: ");
                                String oldWord = scanner.nextLine();
                                System.out.print("Enter the new word: ");
                                String newWord = scanner.nextLine();
                                if (!s1.contains(oldWord)) {
                                    System.out.println("Word not found in the string.");
                                    break;
                                }
                                s1 = s1.replaceFirst(oldWord, newWord);
                            } else {
                                System.out.println("Invalid choice.");
                                break;
                            }
                            c1List = encryptString(s1);
                            break;
                
                        case 2:
                            System.out.println("Current String (s2): " + s2);
                            System.out.println("1. Modify a particular letter");
                            System.out.println("2. Modify a word");
                            System.out.print("Enter your choice: ");
                            int modifyType2 = scanner.nextInt();
                            scanner.nextLine(); // Consume the newline character
                
                            if (modifyType2 == 1) {
                                System.out.print("Enter the position of the letter to modify (1-based index): ");
                                int position = scanner.nextInt();
                                scanner.nextLine(); // Consume the newline character
                                if (position <= 0 || position > s2.length()) {
                                    System.out.println("Invalid position.");
                                    break;
                                }
                                System.out.print("Enter the new letter: ");
                                char newLetter = scanner.nextLine().charAt(0);
                                s2 = s2.substring(0, position - 1) + newLetter + s2.substring(position);
                            } else if (modifyType2 == 2) {
                                System.out.print("Enter the word to replace: ");
                                String oldWord = scanner.nextLine();
                                System.out.print("Enter the new word: ");
                                String newWord = scanner.nextLine();
                                if (!s2.contains(oldWord)) {
                                    System.out.println("Word not found in the string.");
                                    break;
                                }
                                s2 = s2.replaceFirst(oldWord, newWord);
                            } else {
                                System.out.println("Invalid choice.");
                                break;
                            }
                            c2List = encryptString(s2);
                            break;
                
                        default:
                            System.out.println("Invalid choice. No modifications made.");
                            break;
                    }
                    combinedResults = combineCiphertexts(c1List, c2List);
                    System.out.println("Updated Ciphertext: " + combinedResults);
                    break;
                
                case 4:
                    String decrypted = decryptCombinedCiphertext(combinedResults);
                    System.out.println("Decrypted Result: " + decrypted);
                    
                    break;
                case 5:
                    System.out.println("Exiting...");
                    scanner.close();
                    return; 
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }       
        
    }
}
