import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ScholarshipChecker.java
 * ------------------------------------------------------------------
 * A command-line program that determines which Philippine scholarship
 * program(s) a student is eligible for, based on:
 *       - Sex (Female / Male)
 *       - Annual household income
 *       - Academic grade (General Weighted Average, 0-100 scale)
 *       - Target course
 *       - Senior High School STEM strand status  
 * Scholarships evaluated:
 *       1. DOST-Undergraduate Scholarship
 *       2. SM Foundation College Scholarship
 *       3. Megaworld Foundation Scholarship
 *       4. Aboitiz Future Leaders Scholarship Program (ACSP)
 *       5. Ched Merit Scholarship
 *       6. CHED Bagong Pilipinas Merit Scholarship Program (BPMSP)
 *       7. Ayala Foundation U-GO Scholarship Program
 *       8. Grab (to add)
 *       9. Tertiary Education Subsidy (to add)
 *       10. Tulong Dulong Program (to add)
 *       11. Gokongwei Brothers Foundation STEM Scholarship (to add)
 *       12. Jollibee Group Foundation (to add)
 *       13. San Miguel Foundation (to add)
 * ------------------------------------------------------------------
 */
public class ScholarshipChecker {

    // Number of scholarships this program currently evaluates.
    // Update this when a "(to add)" program is implemented.
    static final int EVALUATED_SCHOLARSHIPS = 7;

    // ================================================================
    //   COURSE DATA
    // ================================================================
    static class Course {
        int number;
        String name;
        boolean isDOSTApproved;
        boolean isSMApproved;
        boolean isMegaworldApproved;
        boolean isAboitizApproved;
        boolean isChedBpApproved;
        boolean isAyalaApproved;

        Course(int number, String name, boolean isDOSTApproved, boolean isSMApproved, boolean isMegaworldApproved, 
               boolean isAboitizApproved, boolean isChedBpApproved, boolean isAyalaApproved) {
            this.number = number;
            this.name = name;
            this.isDOSTApproved = isDOSTApproved;
            this.isSMApproved = isSMApproved;
            this.isMegaworldApproved = isMegaworldApproved;
            this.isAboitizApproved = isAboitizApproved;
            this.isChedBpApproved = isChedBpApproved;
            this.isAyalaApproved = isAyalaApproved;
        }
    }

    // Comprehensive prefixed course list, manually sorted alphabetically 
    static final Course[] COURSES = {
        //             Course Name                                                                                               DOST                 SM         Mega Aboitiz CHED Ayala
        new Course(1,  "B Early Childhood Education",                                               false, true,  false, false,  true,  false),
        new Course(2,  "B Fine Arts",                                                               false, false, false, false,  false, false),
        new Course(3,  "B Fine Arts Major in Industrial Design",                                    true,  false, false, false,  false, false),
        new Course(4,  "B Laws / Juris Doctor",                                                     false, false, false, false,  false, false),
        new Course(5,  "B Library and Information Science",                                         true,  false, false, false,  true,  false),
        new Course(6,  "B Special Needs Education / Early Childhood / Teacher Education (STEM)",    false,  true,  false, false,  true,  true),
        new Course(7,  "BA Advertising and Public Relations",                                       false, false, true,  true,   true,  false),
        new Course(8,  "BA Communication / Communication Research / Journalism",                    false, false, false, true,   true,  true),
        new Course(9,  "BA English Language Studies",                                               false, false, false, false,  false, false),
        new Course(10, "BA Indigenous Peoples Studies / Education",                                 false, false, false, false,  true,  false),
        new Course(11, "BA Peace Studies / Education",                                              false, false, false, false,  true,  false),
        new Course(12, "BA Political Science",                                                      false, false, false, true,   true,  false),
        new Course(13, "BA Sociology",                                                              false, false, false, false,  false, false),
        new Course(14, "BS Accountancy",                                                            false,  true,  true,  true,   true,  false),
        new Course(15, "BS Accounting Information Systems",                                         false, true,  true,  true,   true,  false),
        new Course(16, "BS Accounting Technology",                                                  false, true,  true,  true,   true,  false),
        new Course(17, "BS Agribusiness / Agribusiness Economics / Agribusiness Management",        true,  false, false, false,  true,  false),
        new Course(18, "BS Agricultural and Applied Economics",                                     true,  false, false, false,  false, false),
        new Course(19, "BS Agricultural and Biosystems Engineering",                                true,  false, false, false,  false, false),
        new Course(20, "BS Agricultural Chemistry / Engineering / Technology",                      true,  false, false, false,  false, false),
        new Course(21, "BS Agriculture / Agro-Forestry",                                            true,  false, false, true,   true,  false),
        new Course(22, "BS Animal Husbandry / Animal Science",                                      true,  false, false, false,  false, false),
        new Course(23, "BS Applied Mathematics / Applied Statistics",                               true,  false, false, false,  true,  false),
        new Course(24, "BS Applied Physics",                                                        true,  false, false, false,  true,  false),
        new Course(25, "BS Architecture",                                                           true,  false, true,  false,  true,  false),
        new Course(26, "BS Artificial Intelligence",                                                true,  false, false, false,  false, false),
        new Course(27, "BS Astronomy",                                                              true,  false, false, false,  false, false),
        new Course(28, "BS Biochemistry / Biology / Life Sciences",                                 true,  false, false, false,  true,  false),
        new Course(29, "BS Biomedical Engineering / Biotechnology",                                 true,  false, false, false,  false, false),
        new Course(30, "BS Building Management",                                                    false, false, true,  false,  false, false),
        new Course(31, "BS Business Analytics / Business Administration",                           false,  true,  true,  true,   true,  false),
        new Course(32, "BS Ceramics Engineering / Chemical Engineering",                            true,  false, false, true,   true,  false),
        new Course(33, "BS Chemistry / Chemistry for Teachers",                                     true,  false, false, false,  true,  false),
        new Course(34, "BS Civil Engineering",                                                      true,  true,  false, true,   true,  false),
        new Course(35, "BS Clothing Technology / Community Nutrition",                              true,  false, false, false,  false, false),
        new Course(36, "BS Community Development",                                                  false, false, false, false,  true,  false),
        new Course(37, "BS Computer Engineering / Computer Science",                                true,  true,  true,  true,   true,  false),
        new Course(38, "BS Criminology",                                                            false, false, false, false,  false, false),
        new Course(39, "BS Cybersecurity",                                                          true,  false, false, false,  true,  false),
        new Course(40, "BS Data Science and Analytics",                                             true,  false, false, true,   true,  false),
        new Course(41, "BS Development Communication",                                              true,  false, false, false,  false, false),
        new Course(42, "BS Disaster Risk Management / Climate Change",                              false, false, false, false,  true,  false),
        new Course(43, "BS Economics",                                                              false, false, false, true,   true,  false),
        new Course(44, "BS Electrical Engineering",                                                 true,  true,  false, true,   true,  false),
        new Course(45, "BS Electronics Engineering / Electronics and Communications Engineering",   true,  true,  false, true,   true,  false),
        new Course(46, "BS Energy Systems Management",                                              true,  false, false, false,  false, false),
        new Course(47, "BS Engineering Technology / Industrial Technology",                         false, false, false, false,  true,  false),
        new Course(48, "BS Entertainment and Multimedia Computing / Game Development",              false, false, false, false,  true,  false),
        new Course(49, "BS Entrepreneurship",                                                       false, false, false, false,  false, false),
        new Course(50, "BS Environmental Engineering / Environmental Science",                      true,  false, false, false,  true,  false),
        new Course(51, "BS Financial Management",                                                   false, true,  true,  true,   true,  false),
        new Course(52, "BS Fisheries / Fisheries and Aquatic Sciences",                             true,  false, false, false,  true,  false),
        new Course(53, "BS Food Science and Technology / Food Technology / Food Engineering",       true,  false, false, false,  true,  false),
        new Course(54, "BS Forestry",                                                               true,  false, false, true,   true,  false),
        new Course(55, "BS Geodetic Engineering / Geothermal Engineering",                          true,  false, false, true,   true,  false),
        new Course(56, "BS Geography / Geology",                                                    true,  false, false, false,  true,  false),
        new Course(57, "BS Health Sciences / Public Health / Medical Technology",                   true,  false, false, false,  true,  true),
        new Course(58, "BS Hospitality Management / Tourism Management",                            false, false, true,  false,  true,  false),
        new Course(59, "BS Human Services (Guidance and Counseling)",                               false, false, false, false,  true,  false),
        new Course(60, "BS Industrial Design / Industrial Engineering",                             true,  false, false, true,   true,  false),
        new Course(61, "BS Industrial Pharmacy",                                                    true,  false, false, false,  false, false),
        new Course(62, "BS Information Systems / Information Technology",                           true,  true,  false, true,   true,  false),
        new Course(63, "BS Instrumentation and Control Engineering",                                true,  false, false, false,  false, false),
        new Course(64, "BS Interior Design",                                                        false, false, true,  false,  false, false),
        new Course(65, "BS Internal Auditing",                                                      false, true,  true,  false,  false, false),
        new Course(66, "BS Legal Management",                                                       false, false, false, false,  false, false),
        new Course(67, "BS Management Accounting",                                                  false, true,  true,  true,   true,  false),
        new Course(68, "BS Manufacturing Engineering / Materials Engineering",                      true,  false, false, true,   true,  false),
        new Course(69, "BS Marine Biology / Marine Science / Maritime Education",                   true,  false, false, false,  true,  false),
        new Course(70, "BS Marketing",                                                              false, false, true,  false,  false, false),
        new Course(71, "BS Mechanical Engineering",                                                 true,  true,  true,  true,   true,  false),
        new Course(72, "BS Mechatronics Engineering / Robotics Engineering",                        true,  false, false, false,  true,  false),
        new Course(73, "BS Metallurgical Engineering / Mining Engineering / Petroleum Engineering", true,  false, false, true,   true,  false),
        new Course(74, "BS Meteorology / Volcano and Seismology Related Fields",                    true,  false, false, false,  true,  false),
        new Course(75, "BS Microbiology",                                                           true,  false, false, false,  false, false),
        new Course(76, "BS Molecular Biology and Biotechnology",                                    true,  false, false, false,  true,  false),
        new Course(77, "BS Multimedia Arts",                                                        false, false, false, false,  true,  false),
        new Course(78, "BS Naval Architecture and Marine Engineering",                              true,  false, false, false,  true,  false),
        new Course(79, "BS Nursing",                                                                false, false, false, false,  true,  false),
        new Course(80, "BS Nutrition / Nutrition and Dietetics",                                    true,  false, false, false,  true,  false),
        new Course(81, "BS Occupational Therapy / Physical Therapy / Speech Pathology",             true,  false, false, false,  true,  false),
        new Course(82, "BS Pharmaceutical Sciences / Pharmacy",                                     true,  false, false, false,  true,  false),
        new Course(83, "BS Physics / Physics for Teachers",                                         true,  false, false, false,  true,  false),
        new Course(84, "BS Psychology",                                                             true,  false, false, true,   true,  false),
        new Course(85, "BS Public Administration",                                                  false, false, false, false,  false, false),
        new Course(86, "BS Railway Engineering and Management",                                     true,  false, false, false,  false, false),
        new Course(87, "BS Renewable / Sustainable Energy",                                         false, false, false, false,  true,  false),
        new Course(88, "BS Social Work",                                                            false, false, false, false,  false, false),
        new Course(89, "BS Sports and Exercise Science / Sports Science",                           true,  false, false, false,  true,  false),
        new Course(90, "BS Statistics / Applied Statistics",                                        true,  false, false, false,  true,  false),
        new Course(91, "BS Transportation Engineering",                                             true,  false, false, false,  false, false),
        new Course(92, "BS Veterinary Medicine",                                                    true,  false, false, false,  false, false),
        new Course(93, "Not included",                                                              false, false, false, false,  false, false)
    };

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            run(scanner);
        } catch (NoSuchElementException e) {
            System.out.println("\nInput ended before all questions were answered. Exiting.");
        }
    }

    private static void run(Scanner scanner) {

        System.out.println("=========================================================");
        System.out.println("   PHILIPPINE SCHOLARSHIP ELIGIBILITY CHECKER");
        System.out.println("=========================================================\n");

        // ---------------- Name ----------------
        System.out.print("Enter your full name (LASTNAME, FirstName M.I.): ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("ERROR! Name cannot be blank. Enter your full name: ");
            name = scanner.nextLine().trim();
        }

        // ---------------- Sex ----------------
        String sex = readSex(scanner);

        // ---------------- Annual Income ----------------
        double income = readDouble(scanner,
                "Enter your family's total ANNUAL household income (in PHP, numbers only): ");

        // ---------------- Grades ----------------
        double grade = readGrade(scanner,
                "Enter your General Weighted Average / grade (0-100 scale): ");

        // ---------------- STEM Strand Status ----------------
        boolean isStemStudent = readYesNo(scanner,
                "Are you a Senior High School STEM strand student/graduate? (Y/N): ");

        // ---------------- Target Course ----------------
        printCourseList();
        int courseChoice = readCourseChoice(scanner);
        Course chosen = COURSES[courseChoice - 1];

        // If "Not included" is chosen (last option), user is automatically disqualified from all
        boolean isNotIncluded = (courseChoice == COURSES.length);

        // ---------------- Evaluate Eligibility ----------------
        String[] eligibleNames = new String[EVALUATED_SCHOLARSHIPS];
        String[] eligibleBenefits = new String[EVALUATED_SCHOLARSHIPS];
        int eligibleCount = 0;

        if (!isNotIncluded) {
            // 1. DOST-Undergraduate Scholarship
            boolean dostEligible = chosen.isDOSTApproved;
            if (dostEligible) {
                String dostScholarshipType = (income <= 400_000)
                    ? "- You are eligible for RA 7687 and MERIT scholarship.\n"
                    : "- You are eligible for MERIT scholarship.\n";
                String dostNote = isStemStudent
                    ? ""
                    : "   - NOTE: As a non-STEM strand applicant, you must belong to the upper 10% of your graduating class to qualify.\n" +
                      "     This requirement can be confirmed with your school.";
                eligibleNames[eligibleCount] = "DOST-Undergraduate Scholarship";
                eligibleBenefits[eligibleCount] =
                    dostScholarshipType +
                    "   - Full tuition and other school fees\n" +
                    "   - Monthly stipend (PHP 8,000)\n" +
                    "   - Book & Connectivity Allowance (PHP 10,000/year)\n" +
                    "   - Transportation and thesis/graduating allowance\n" +
                    "   - Summer/on-the-job training allowance\n" +
                    dostNote;
                eligibleCount++;
            }

            // 2. SM Foundation College Scholarship
            boolean smEligible = chosen.isSMApproved && income <= 150_000 && grade >= 88.0;
            if (smEligible) {
                eligibleNames[eligibleCount] = "SM Foundation College Scholarship";
                eligibleBenefits[eligibleCount] =
                    "- Full tuition and miscellaneous fees\n" +
                    "   - Book allowance\n" +
                    "   - Monthly stipend\n" +
                    "   - Placement in an SM partner college/university\n" +
                    "   - Possible employment opportunities within the SM Group after graduation";
                eligibleCount++;
            }

            // 3. Megaworld Foundation Scholarship
            boolean megaworldEligible = chosen.isMegaworldApproved && income <= 400_000 && grade >= 85.0;
            if (megaworldEligible) {
                eligibleNames[eligibleCount] = "Megaworld Foundation Scholarship";
                eligibleBenefits[eligibleCount] =
                    "- Full or partial tuition coverage\n" +
                    "   - Allowances for books and other educational needs\n" +
                    "   - Internship and career opportunities within Megaworld Corporation";
                eligibleCount++;
            }

            // 4. Aboitiz College Scholarship Program (ACSP)
            boolean aboitizEligible = chosen.isAboitizApproved && grade >= 88.0;
            if (aboitizEligible) {
                eligibleNames[eligibleCount] = "Aboitiz College Scholarship Program (ACSP)";
                eligibleBenefits[eligibleCount] =
                    "- Full tuition and fee coverage\n" +
                    "   - Annual book and allowance subsidy\n" +
                    "   - OJT and direct employment opportunities within the Aboitiz Group";
                eligibleCount++;
            }

            // 5. CHED Merit Scholarship Program
            boolean chedEligible = chosen.isChedBpApproved && grade >= 85.0;
            if (chedEligible) {
                eligibleNames[eligibleCount] = "CHED Merit Scholarship Program";
                eligibleBenefits[eligibleCount] =
                    "- Tuition and other school fee subsidy (up to PHP 60,000/year)\n" +
                    "   - Cash stipend allowance\n" +
                    "   - Must be enrolled in a CHED recognized priority program";
                eligibleCount++;
            }

            // 6. CHED Bagong Pilipinas Merit Scholarship Program (BPMSP)
            boolean chedBpEligible = chosen.isChedBpApproved && income <= 2_000_000 && grade >= 95.0;
            if (chedBpEligible) {
                eligibleNames[eligibleCount] = "CHED Bagong Pilipinas Merit Scholarship Program (BPMSP)";
                eligibleBenefits[eligibleCount] =
                    "- Full coverage of tuition and other school fees\n" +
                    "   - Generous annual living stipend and allowances\n" +
                    "   - Academic excellence incentives for top performers";
                eligibleCount++;
            }

            // 7. Ayala U-GO Scholarship Program
            boolean isFemale = sex.equalsIgnoreCase("Female"); // Aligned with the output from readSex
            boolean ayalaUgoEligible = isFemale && grade >= 85.0 && chosen.isAyalaApproved;
            if (ayalaUgoEligible) {
                eligibleNames[eligibleCount] = "Ayala U-GO Scholarship Program";
                eligibleBenefits[eligibleCount] =
                    "- Financial grant entitlement (approx. PHP 40,000 renewable grant)\n" +
                    "   - Exclusively for female students demonstrating financial need in public colleges/universities\n" +
                    "   - Specifically supports priority fields like Communication, Education, and Health Sciences";
                eligibleCount++;
            }
        }

        // ---------------- Generate User ID ----------------
        String userId = generateUserId();

        // ---------------- Display Results ----------------
        System.out.println();
        System.out.println("=========================================================");
        System.out.println("                    RESULTS SUMMARY");
        System.out.println("=========================================================");
        System.out.println("Name             : " + name);
        System.out.println("Sex              : " + sex);
        System.out.printf ("Annual Income    : PHP %,.2f%n", income);
        System.out.printf ("Grade (GWA)      : %.2f%n", grade);
        System.out.println("STEM Strand      : " + (isStemStudent ? "Yes" : "No"));
        System.out.println("Target Course    : " + chosen.number + " - " + chosen.name);
        System.out.println("USER ID          : " + userId);
        System.out.println("---------------------------------------------------------");

        if (isNotIncluded) {
            System.out.println("Result: Your preferred course is not included in the list");
            System.out.println("        of approved programs for the scholarships evaluated.");
            System.out.println("        Therefore, you are not qualified for any of these scholarships.");
        } else if (eligibleCount == 0) {
            System.out.println("Result: You are not currently eligible for any of the");
            System.out.println("        scholarships evaluated by this program based on");
            System.out.println("        your current income, grades, course, or profile.");
        } else {
            System.out.println("You are ELIGIBLE for the following scholarship(s):");
            System.out.println();
            for (int i = 0; i < eligibleCount; i++) {
                System.out.println((i + 1) + ". " + eligibleNames[i]);
                System.out.println("   Benefits:");
                System.out.println("   " + eligibleBenefits[i]);
                System.out.println();
            }
        }

        System.out.println("---------------------------------------------------------");
        System.out.println("TOTAL SCHOLARSHIPS ELIGIBLE FOR: " + eligibleCount + " out of " + EVALUATED_SCHOLARSHIPS);
        System.out.println("=========================================================");

    }

    // ================================================================
    //   HELPER METHODS
    // ================================================================

    /** Prints the full numbered alphabetical list of available courses including the final option. */
    private static void printCourseList() {
        System.out.println();
        System.out.println("=========================================================");
        System.out.println("                      AVAILABLE COURSES");
        System.out.println("=========================================================");
        for (Course c : COURSES) {
            System.out.printf("%2d. %s%n", c.number, c.name);
        }
        System.out.println("=========================================================");
    }

    /** Reads and validates the user's sex input (Female/Male or F/M). */
    private static String readSex(Scanner scanner) {
        while (true) {
            System.out.print("Enter your sex (F or M): ");
            String input = scanner.nextLine().trim().toUpperCase();
            
            switch (input) {
                case "F":
                    return "Female";
                case "M":
                    return "Male";
                default:
                    System.out.println("Invalid input. Please enter 'F' or 'M'.");
            }
        }
    }

    /** Reads and validates the course selection number. */
    private static int readCourseChoice(Scanner scanner) {
        int choice = -1;
        while (true) {
            System.out.print("Enter the NUMBER of your target course: ");
            String line = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(line);
                if (choice >= 1 && choice <= COURSES.length) {
                    break;
                } else {
                    System.out.println("Please enter a number between 1 and " + COURSES.length + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid course number.");
            }
        }
        return choice;
    }

    /** Reads and validates a non-negative income value. */
    private static double readDouble(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(",", "");
            try {
                value = Double.parseDouble(line);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    System.out.println("Invalid input. Please enter numbers only (e.g., 150000).");
                    continue;
                }
                if (value < 0) {
                    System.out.println("Income cannot be negative. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only (e.g., 150000).");
            }
        }
        return value;
    }

    /** Reads and validates a Yes/No response, returning true for Yes. */
    private static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toUpperCase();
            
            switch (line) {
                case "Y":
                    return true;
                case "N":
                    return false;
                default:
                    System.out.println("Invalid input. Please enter Y or N.");
            }
        }
    }


    /** Reads and validates a grade between 0 and 100. */
    private static double readGrade(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                value = Double.parseDouble(line);
                if (Double.isNaN(value) || value < 0 || value > 100) {
                    System.out.println("Grade must be between 0 and 100. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric grade (e.g., 91.5).");
            }
        }
        return value;
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    /** Generates a random 8-character alphanumeric USER ID. */
    private static String generateUserId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = RANDOM.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}