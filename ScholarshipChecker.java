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
 *       - TES priority-group status (Listahanan/4Ps, solo-parent dependent, IP, PWD)
 *       - Senior High School STEM strand status  
 * Scholarships evaluated:
 *       1. DOST-Undergraduate Scholarship
 *       2. SM Foundation College Scholarship
 *       3. Megaworld Foundation Scholarship
 *       4. Aboitiz Future Leaders Scholarship Program (ACSP)
 *       5. Ched Merit Scholarship
 *       6. CHED Bagong Pilipinas Merit Scholarship Program (BPMSP)
 *       7. Ayala Foundation U-GO Scholarship Program
 *       8. GrabScholar College Scholarship
 *       9. CHED Tertiary Education Subsidy (TES)
 *       10. CHED Tulong Dunong Program (TDP)
 *       11. Gokongwei Brothers Foundation (GBF) STEM College Scholarship
 *       12. Jollibee Group Foundation ACE Scholarship
 *       13. San Miguel Foundation Scholarship
 * ------------------------------------------------------------------
 */
public class ScholarshipChecker {

    // Number of scholarships this program currently evaluates.
    // Update this when a "(to add)" program is implemented.
    static final int EVALUATED_SCHOLARSHIPS = 13;

    // Assumed income ceiling for "need-based" programs that publish no exact cap
    // (Gokongwei STEM, Jollibee ACE, San Miguel). 400,000 matches the TDP / RA 7687 benchmark.
    // Adjust this one value if you find an official figure.
    static final double NEED_BASED_INCOME_CEILING = 400_000;

    // ================================================================
    //   COURSE DATA
    // ================================================================

    // A Course is a simple "record": its number, its name, and one true/false
    // flag per scholarship saying whether that scholarship accepts the course.
    static class Course {
        int number;
        String name;
        boolean isDOSTApproved;
        boolean isSMApproved;
        boolean isMegaworldApproved;
        boolean isAboitizApproved;
        boolean isChedBpApproved;
        boolean isAyalaApproved;
        boolean isGrabApproved;
        boolean isGokongweiApproved;
        boolean isJollibeeApproved;
        boolean isSanMiguelApproved;

        // Constructor: copies each value into the object's fields
        Course(int number, String name, boolean isDOSTApproved, boolean isSMApproved, boolean isMegaworldApproved, 
               boolean isAboitizApproved, boolean isChedBpApproved, boolean isAyalaApproved,
               boolean isGrabApproved, boolean isGokongweiApproved, boolean isJollibeeApproved,
               boolean isSanMiguelApproved) {
            this.number = number;
            this.name = name;
            this.isDOSTApproved = isDOSTApproved;
            this.isSMApproved = isSMApproved;
            this.isMegaworldApproved = isMegaworldApproved;
            this.isAboitizApproved = isAboitizApproved;
            this.isChedBpApproved = isChedBpApproved;
            this.isAyalaApproved = isAyalaApproved;
            this.isGrabApproved = isGrabApproved;
            this.isGokongweiApproved = isGokongweiApproved;
            this.isJollibeeApproved = isJollibeeApproved;
            this.isSanMiguelApproved = isSanMiguelApproved;
        }
    }

    // Master list of courses (array). Each row = one course + which scholarships accept it.
    // The last entry, "Not included", is approved by nothing (automatic disqualification).
    static final Course[] COURSES = {
        //             Course Name                                                                                               DOST                 SM         Mega Aboitiz CHED Ayala
        new Course(1,  "B Early Childhood Education",                                               false, true,  false, false,  true,  false,   false, false, false, false),
        new Course(2,  "B Fine Arts",                                                               false, false, false, false,  false, false,   false, false, false, false),
        new Course(3,  "B Fine Arts Major in Industrial Design",                                    true,  false, false, false,  false, false,   false, false, false, false),
        new Course(4,  "B Laws / Juris Doctor",                                                     false, false, false, false,  false, false,   false, false, false, false),
        new Course(5,  "B Library and Information Science",                                         true,  false, false, false,  true,  false,   false, false, false, false),
        new Course(6,  "B Special Needs Education / Early Childhood / Teacher Education (STEM)",    false,  true,  false, false,  true,  true,   false, false, false, false),
        new Course(7,  "BA Advertising and Public Relations",                                       false, false, true,  true,   true,  false,   false, false, false, false),
        new Course(8,  "BA Communication / Communication Research / Journalism",                    false, false, false, true,   true,  true,   false, false, false, false),
        new Course(9,  "BA English Language Studies",                                               false, false, false, false,  false, false,   false, false, false, false),
        new Course(10, "BA Indigenous Peoples Studies / Education",                                 false, false, false, false, true, false,   false, false, false, false),
        new Course(11, "BA Peace Studies / Education",                                              false, false, false, false, true, false,   false, false, false, false),
        new Course(12, "BA Political Science",                                                      false, false, false, true, true, false,   false, false, false, false),
        new Course(13, "BA Sociology",                                                              false, false, false, false, false, false,   false, false, false, false),
        new Course(14, "BS Accountancy",                                                            false, true, true, true, true, false,   true, true, false, true),
        new Course(15, "BS Accounting Information Systems",                                         false, true, true, true, true, false,   true, false, false, false),
        new Course(16, "BS Accounting Technology",                                                  false, true, true, true, true, false,   true, false, false, false),
        new Course(17, "BS Agribusiness / Agribusiness Economics / Agribusiness Management",        true, false, false, false, true, false,   false, false, true, false),
        new Course(18, "BS Agricultural and Applied Economics",                                     true, false, false, false, false, false,   false, false, false, false),
        new Course(19, "BS Agricultural and Biosystems Engineering",                                true, false, false, false, false, false,   true, false, false, false),
        new Course(20, "BS Agricultural Chemistry / Engineering / Technology",                      true, false, false, false, false, false,   true, false, false, false),
        new Course(21, "BS Agriculture / Agro-Forestry",                                            true, false, false, true, true, false,   true, false, true, false),
        new Course(22, "BS Animal Husbandry / Animal Science",                                      true, false, false, false, false, false,   false, true, false, false),
        new Course(23, "BS Applied Mathematics / Applied Statistics",                               true, false, false, false, true, false,   true, false, false, false),
        new Course(24, "BS Applied Physics",                                                        true, false, false, false, true, false,   true, false, false, false),
        new Course(25, "BS Architecture",                                                           true, false, true, false, true, false,   false, false, false, false),
        new Course(26, "BS Artificial Intelligence",                                                true, false, false, false, false, false,   true, false, false, false),
        new Course(27, "BS Astronomy",                                                              true, false, false, false, false, false,   true, false, false, false),
        new Course(28, "BS Biochemistry / Biology / Life Sciences",                                 true, false, false, false, true, false,   true, false, false, false),
        new Course(29, "BS Biomedical Engineering / Biotechnology",                                 true, false, false, false, false, false,   false, false, false, false),
        new Course(30, "BS Building Management",                                                    false, false, true, false, false, false,   false, false, false, false),
        new Course(31, "BS Business Analytics / Business Administration",                           false, true, true, true, true, false,   true, false, false, true),
        new Course(32, "BS Ceramics Engineering / Chemical Engineering",                            true, false, false, true, true, false,   true, true, false, true),
        new Course(33, "BS Chemistry / Chemistry for Teachers",                                     true, false, false, false, true, false,   true, true, false, true),
        new Course(34, "BS Civil Engineering",                                                      true, true, false, true, true, false,   true, true, false, true),
        new Course(35, "BS Clothing Technology / Community Nutrition",                              true, false, false, false, false, false,   false, false, false, false),
        new Course(36, "BS Community Development",                                                  false, false, false, false, true, false,   true, false, false, false),
        new Course(37, "BS Computer Engineering / Computer Science",                                true, true, true, true, true, false,   true, true, false, false),
        new Course(38, "BS Criminology",                                                            false, false, false, false, false, false,   false, false, false, false),
        new Course(39, "BS Cybersecurity",                                                          true, false, false, false, true, false,   true, false, false, false),
        new Course(40, "BS Data Science and Analytics",                                             true, false, false, true, true, false,   true, true, false, false),
        new Course(41, "BS Development Communication",                                              true, false, false, false, false, false,   false, false, false, false),
        new Course(42, "BS Disaster Risk Management / Climate Change",                              false, false, false, false, true, false,   true, false, false, false),
        new Course(43, "BS Economics",                                                              false, false, false, true, true, false,   true, false, false, true),
        new Course(44, "BS Electrical Engineering",                                                 true, true, false, true, true, false,   true, true, false, true),
        new Course(45, "BS Electronics Engineering / Electronics and Communications Engineering",   true, true, false, true, true, false,   true, false, false, false),
        new Course(46, "BS Energy Systems Management",                                              true, false, false, false, false, false,   false, false, false, false),
        new Course(47, "BS Engineering Technology / Industrial Technology",                         false, false, false, false, true, false,   false, false, false, false),
        new Course(48, "BS Entertainment and Multimedia Computing / Game Development",              false, false, false, false, true, false,   true, false, false, false),
        new Course(49, "BS Entrepreneurship",                                                       false, false, false, false, false, false,   true, false, false, false),
        new Course(50, "BS Environmental Engineering / Environmental Science",                      true, false, false, false, true, false,   true, false, false, true),
        new Course(51, "BS Financial Management",                                                   false, true, true, true, true, false,   true, false, false, false),
        new Course(52, "BS Fisheries / Fisheries and Aquatic Sciences",                             true, false, false, false, true, false,   false, false, false, false),
        new Course(53, "BS Food Science and Technology / Food Technology / Food Engineering",       true, false, false, false, true, false,   true, false, false, false),
        new Course(54, "BS Forestry",                                                               true, false, false, true, true, false,   true, false, false, false),
        new Course(55, "BS Geodetic Engineering / Geothermal Engineering",                          true, false, false, true, true, false,   true, false, false, false),
        new Course(56, "BS Geography / Geology",                                                    true, false, false, false, true, false,   true, false, false, false),
        new Course(57, "BS Health Sciences / Public Health / Medical Technology",                   true, false, false, false, true, true,   false, false, false, false),
        new Course(58, "BS Hospitality Management / Tourism Management",                            false, false, true, false, true, false,   false, false, false, false),
        new Course(59, "BS Human Services (Guidance and Counseling)",                               false, false, false, false, true, false,   false, false, false, false),
        new Course(60, "BS Industrial Design / Industrial Engineering",                             true, false, false, true, true, false,   true, true, false, true),
        new Course(61, "BS Industrial Pharmacy",                                                    true, false, false, false, false, false,   false, false, false, false),
        new Course(62, "BS Information Systems / Information Technology",                           true, true, false, true, true, false,   true, true, false, true),
        new Course(63, "BS Instrumentation and Control Engineering",                                true, false, false, false, false, false,   true, false, false, false),
        new Course(64, "BS Interior Design",                                                        false, false, true, false, false, false,   false, false, false, false),
        new Course(65, "BS Internal Auditing",                                                      false, true, true, false, false, false,   true, false, false, false),
        new Course(66, "BS Legal Management",                                                       false, false, false, false, false, false,   false, false, false, false),
        new Course(67, "BS Management Accounting",                                                  false, true, true, true, true, false,   true, false, false, false),
        new Course(68, "BS Manufacturing Engineering / Materials Engineering",                      true, false, false, true, true, false,   true, false, false, false),
        new Course(69, "BS Marine Biology / Marine Science / Maritime Education",                   true, false, false, false, true, false,   true, false, false, false),
        new Course(70, "BS Marketing",                                                              false, false, true, false, false, false,   true, false, false, false),
        new Course(71, "BS Mechanical Engineering",                                                 true, true, true, true, true, false,   true, true, false, true),
        new Course(72, "BS Mechatronics Engineering / Robotics Engineering",                        true, false, false, false, true, false,   true, false, false, false),
        new Course(73, "BS Metallurgical Engineering / Mining Engineering / Petroleum Engineering", true, false, false, true, true, false,   true, false, false, false),
        new Course(74, "BS Meteorology / Volcano and Seismology Related Fields",                    true, false, false, false, true, false,   true, false, false, false),
        new Course(75, "BS Microbiology",                                                           true, false, false, false, false, false,   true, false, false, false),
        new Course(76, "BS Molecular Biology and Biotechnology",                                    true, false, false, false, true, false,   true, false, false, false),
        new Course(77, "BS Multimedia Arts",                                                        false, false, false, false, true, false,   false, false, false, false),
        new Course(78, "BS Naval Architecture and Marine Engineering",                              true, false, false, false, true, false,   true, false, false, false),
        new Course(79, "BS Nursing",                                                                false, false, false, false, true, false,   false, false, false, false),
        new Course(80, "BS Nutrition / Nutrition and Dietetics",                                    true, false, false, false, true, false,   false, false, false, false),
        new Course(81, "BS Occupational Therapy / Physical Therapy / Speech Pathology",             true, false, false, false, true, false,   false, false, false, false),
        new Course(82, "BS Pharmaceutical Sciences / Pharmacy",                                     true, false, false, false, true, false,   false, true, false, false),
        new Course(83, "BS Physics / Physics for Teachers",                                         true, false, false, false, true, false,   true, false, false, false),
        new Course(84, "BS Psychology",                                                             true, false, false, true, true, false,   false, false, false, false),
        new Course(85, "BS Public Administration",                                                  false, false, false, false, false, false,   false, false, false, false),
        new Course(86, "BS Railway Engineering and Management",                                     true, false, false, false, false, false,   true, false, false, false),
        new Course(87, "BS Renewable / Sustainable Energy",                                         false, false, false, false, true, false,   true, false, false, false),
        new Course(88, "BS Social Work",                                                            false, false, false, false, false, false,   false, false, false, false),
        new Course(89, "BS Sports and Exercise Science / Sports Science",                           true, false, false, false, true, false,   false, false, false, false),
        new Course(90, "BS Statistics / Applied Statistics",                                        true, false, false, false, true, false,   true, false, false, false),
        new Course(91, "BS Transportation Engineering",                                             true, false, false, false, false, false,   true, false, false, false),
        new Course(92, "BS Veterinary Medicine",                                                    true, false, false, false, false, false,   false, true, false, false),
        new Course(93, "Not included",                                                              false, false, false, false, false, false,   false, false, false, false)
    };

    // ================================================================
    //   MAIN: program entry point
    // ================================================================
    public static void main(String[] args) {
        // Turns on ANSI support in Windows Command Prompt so the screen-clearing codes work
        try {
            new ProcessBuilder("cmd", "/c", "").inheritIO().start().waitFor();
        } catch (Exception ignored) { }

        // try-with-resources: the Scanner is closed automatically when we're done
        try (Scanner scanner = new Scanner(System.in)) {
            boolean tryAgain;

            // do-while: runs the checker at least once, then repeats while the user says Yes
            do {
                run(scanner);   // one full session (questions -> results)

                System.out.println();
                tryAgain = readYesNo(scanner,
                        "Would you like to check another applicant? (Y = try again / N = exit): ");

                if (tryAgain) {
                    clearScreen();   // fresh screen for the next run
                }
            } while (tryAgain);

            System.out.println("\nThank you for using the Philippine Scholarship Eligibility Checker. Goodbye!");
        } catch (NoSuchElementException e) {
            // Happens if the input stream closes unexpectedly (e.g., Ctrl+D / Ctrl+Z)
            System.out.println("\nInput ended before all questions were answered. Exiting.");
        }
    }

    // ================================================================
    //   RUN: one complete session (ask questions, evaluate, show results)
    // ================================================================
    private static void run(Scanner scanner) {

        // ---- Title banner ----
        System.out.println("╔═════════════════════════════════════════════════════════╗");
        System.out.println("║       PHILIPPINE SCHOLARSHIP ELIGIBILITY CHECKER        ║");
        System.out.println("╚═════════════════════════════════════════════════════════╝\n");

        // ---------------- Name ----------------
        // Keep asking until the name is not blank and contains no digits
        System.out.print("Enter your full name (LASTNAME, FirstName M.I.): ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty() || name.matches(".*\\d.*")) {   // regex ".*\\d.*" = "contains a digit"
            if (name.isEmpty()) {
                System.out.print(" ERROR! Name cannot be blank. Enter your full name: ");
            } else {
                System.out.print(" ERROR! Name cannot contain numbers. Enter your full name: ");
            }
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

        // ---------------- TES Priority Group ----------------
        // Needed for the Tertiary Education Subsidy, which ranks applicants by priority group
        boolean isPriorityGroup = readYesNo(scanner,
                "Are you in a TES priority group (Listahanan/4Ps, solo-parent dependent, IP, or PWD)? (Y/N): ");

        // ---------------- Target Course ----------------
        clearScreen();
        int courseChoice = readCourseChoice(scanner);
        Course chosen = COURSES[courseChoice - 1];   // -1 because arrays start at index 0

        // If "Not included" is chosen (last option), user is automatically disqualified from all
        boolean isNotIncluded = (courseChoice == COURSES.length);

        // ---------------- Evaluate Eligibility ----------------
        // Two parallel arrays store each qualified scholarship's name and its benefits.
        // eligibleCount tracks how many slots are filled so far.
        String[] eligibleNames = new String[EVALUATED_SCHOLARSHIPS];
        String[] eligibleBenefits = new String[EVALUATED_SCHOLARSHIPS];
        int eligibleCount = 0;

        if (!isNotIncluded) {
            // 1. DOST-Undergraduate Scholarship
            // Rule: course must be DOST-approved (no income or grade cutoff here)
            boolean dostEligible = chosen.isDOSTApproved;
            if (dostEligible) {
                // Income <= 400,000 qualifies for both RA 7687 and Merit; otherwise Merit only
                String dostScholarshipType = (income <= 400_000)
                    ? "- You are eligible for RA 7687 and MERIT scholarship.\n"
                    : "- You are eligible for MERIT scholarship.\n";
                // Non-STEM applicants get an extra reminder (top 10% of class)
                String dostNote = isStemStudent
                    ? ""
                    : "   - NOTE: As a non-STEM strand applicant, you must belong to the upper 10% of your graduating class to qualify.\n" +
                      "     This requirement can be confirmed with your school.";
                eligibleNames[eligibleCount] = "DOST-Undergraduate Scholarship";
                eligibleBenefits[eligibleCount] =
                    dostScholarshipType +
                    "   - Grant of PHP 40,000 per academic year\n" +
                    "   - Monthly stipend (PHP 8,000)\n" +
                    "   - Book & Connectivity Allowance (PHP 10,000/year)\n" +
                    "   - Transportation and thesis/graduating allowance\n" +
                    "   - Summer/on-the-job training allowance\n" +
                    dostNote;
                eligibleCount++;   // move to the next free slot
            }

            // 2. SM Foundation College Scholarship
            // Rule: approved course AND income <= 150,000 AND grade >= 88
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
            // Rule: approved course AND income <= 400,000 AND grade >= 85
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
            // Rule: approved course AND grade >= 88 (no income limit)
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
            // Rule: CHED-approved course AND grade >= 85 (no income limit)
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
            // Rule: CHED-approved course AND income <= 2,000,000 AND grade >= 95 (stricter grade)
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
            // Rule: female AND grade >= 85 AND Ayala-approved course
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

            // 8. GrabScholar College Scholarship
            // Rule: Business/STEM (non-medical)/Sustainability course AND Grade 12 average >= 90
            boolean grabEligible = chosen.isGrabApproved && grade >= 90.0;
            if (grabEligible) {
                eligibleNames[eligibleCount] = "GrabScholar College Scholarship";
                eligibleBenefits[eligibleCount] =
                    "- 100% tuition and miscellaneous fees (full-ride, merit-based)\n" +
                    "   - School supplies allowance (PHP 11,000/year)\n" +
                    "   - Monthly student allowance (PHP 8,000/month)\n" +
                    "   - Learning and development programs, plus internship opportunities\n" +
                    "   - NOTE: For incoming first-year students only; you must not have another active scholarship";
                eligibleCount++;
            }

            // 9. CHED Tertiary Education Subsidy (TES)
            // Rule: any CHED-recognized course AND belongs to a TES priority group
            boolean tesEligible = isPriorityGroup;
            if (tesEligible) {
                eligibleNames[eligibleCount] = "CHED Tertiary Education Subsidy (TES)";
                eligibleBenefits[eligibleCount] =
                    "- Grant of PHP 10,000 to PHP 27,000 per academic year (depends on priority tier and school type)\n" +
                    "   - Extra PHP 10,000/year for PWD students\n" +
                    "   - One-time licensure exam reimbursement (up to PHP 8,000)\n" +
                    "   - NOTE: Applied for through your school; funds are limited and ranked by priority.\n" +
                    "     Cannot be combined with other national government scholarships (e.g., TDP, DOST, CHED Merit)";
                eligibleCount++;
            }

            // 10. CHED Tulong Dunong Program (TDP)
            // Rule: any CHED-recognized course AND household income <= 400,000
            boolean tdpEligible = income <= 400_000;
            if (tdpEligible) {
                eligibleNames[eligibleCount] = "CHED Tulong Dunong Program (TDP)";
                eligibleBenefits[eligibleCount] =
                    "- Grant of PHP 7,500 per semester (PHP 15,000 per academic year)\n" +
                    "   - For a first undergraduate degree in SUCs, LUCs or CHED-UniFAST partner private schools\n" +
                    "   - Requires a Certificate of Indigency from your Punong Barangay\n" +
                    "   - NOTE: Cannot be combined with TES or other national government scholarships";
                eligibleCount++;
            }

            // 11. Gokongwei Brothers Foundation (GBF) STEM College Scholarship
            // Rule: GBF priority STEM course AND grade >= 85 AND financial need (income ceiling)
            boolean gbfEligible = chosen.isGokongweiApproved
                    && grade >= 85.0 && income <= NEED_BASED_INCOME_CEILING;
            if (gbfEligible) {
                eligibleNames[eligibleCount] = "GBF STEM College Scholarship (Gokongwei)";
                eligibleBenefits[eligibleCount] =
                    "- Fixed annual grant of PHP 80,000 to PHP 120,000 (depends on your university)\n" +
                    "   - Scholar development programs (leadership training, upskilling)\n" +
                    "   - Internship in a Gokongwei Group company\n" +
                    "   - Employment opportunity in the Gokongwei Group (subject to vacancy)\n" +
                    "   - NOTE: Accepted year levels vary by course; good moral standing and extra-curricular involvement required";
                eligibleCount++;
            }

            // 12. Jollibee Group Foundation (JGF) ACE Scholarship
            // Rule: agriculture/agribusiness course AND financial need (income ceiling)
            boolean jgfEligible = chosen.isJollibeeApproved
                    && income <= NEED_BASED_INCOME_CEILING;
            if (jgfEligible) {
                eligibleNames[eligibleCount] = "Jollibee Group Foundation ACE Scholarship";
                eligibleBenefits[eligibleCount] =
                    "- Scholarship for indigent youth under the Access, Curriculum and Employability (ACE) program\n" +
                    "   - Agro-entrepreneurship training with life-skills and curriculum enhancement\n" +
                    "   - Employability and career support through JGF partner institutions\n" +
                    "   - NOTE: ACE is mainly a technical-vocational (TESDA NC II) program; confirm degree-level slots with JGF";
                eligibleCount++;
            }

            // 13. San Miguel Foundation Scholarship
            // Rule: priority course AND grade >= 88 (incoming freshman standard) AND financial need
            boolean smfEligible = chosen.isSanMiguelApproved
                    && grade >= 88.0 && income <= NEED_BASED_INCOME_CEILING;
            if (smfEligible) {
                eligibleNames[eligibleCount] = "San Miguel Foundation Scholarship";
                eligibleBenefits[eligibleCount] =
                    "- Educational assistance from San Miguel Foundation, Inc.\n" +
                    "   - Possible employment opportunities within the San Miguel group\n" +
                    "   - NOTE: Criteria come from a third-party summary; confirm requirements and benefits with the foundation";
                eligibleCount++;
            }
        }

        // ---------------- Generate User ID ----------------
        String userId = generateUserId();

        // ---------------- Display Results ----------------
        clearScreen();
        System.out.println("╔═════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESULTS SUMMARY                      ║");
        System.out.println("╚═════════════════════════════════════════════════════════╝");
        // Echo back the applicant's answers
        System.out.println("Name             : " + name);
        System.out.println("Sex              : " + sex);
        System.out.printf ("Annual Income    : PHP %,.2f%n", income);   // %,.2f = comma separators, 2 decimals
        System.out.printf ("Grade (GWA)      : %.2f%n", grade);
        System.out.println("STEM Strand      : " + (isStemStudent ? "Yes" : "No"));
        System.out.println("TES Priority     : " + (isPriorityGroup ? "Yes" : "No"));
        System.out.println("Target Course    : " + chosen.number + " - " + chosen.name);
        System.out.println("USER ID          : " + userId);
        System.out.println("═══════════════════════════════════════════════════════════");

        // Three possible outcomes: course not included / no match / one or more matches
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
            // Loop only over the filled slots and print each scholarship with its benefits
            for (int i = 0; i < eligibleCount; i++) {
                System.out.println((i + 1) + " │ " + eligibleNames[i]);
                System.out.println("   Benefits:");
                System.out.println("   " + eligibleBenefits[i]);
                
            }
        }

        // Final tally box
        System.out.println("╔═════════════════════════════════════════════════════════╗");
        // %-57s pads the text to the box width so the right border stays aligned for 2-digit counts
        System.out.println(String.format("║%-57s║",
                "     TOTAL SCHOLARSHIPS ELIGIBLE FOR: " + eligibleCount + " out of " + EVALUATED_SCHOLARSHIPS));
        System.out.println("╚═════════════════════════════════════════════════════════╝");

    }

    // ================================================================
    //   HELPER METHODS
    // ================================================================

    // Overloaded clear screen helper methods (same name, different parameters)

    // Version 1: short pause, then clear the screen
    public static void clearScreen() {
        try {
            Thread.sleep(1500);   // wait 1.5 seconds so the user can read the last message
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // ANSI codes: move cursor home, clear screen, clear scrollback
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    // Version 2: same thing, but the caller chooses how many seconds to wait
    public static void clearScreen(int seconds) throws InterruptedException {
        if (seconds > 0) {
            Thread.sleep(seconds * 1000L);
        }
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    /** Prints a paginated list of available courses (30 per page for 3 pages). */
    private static void printCoursePage(int page) {
        int totalPages = 3;
        int pageSize = 30;
        int start = (page - 1) * pageSize;   // first course index on this page
        int end = (page == totalPages) ? COURSES.length : start + pageSize;   // last page shows the leftovers

        System.out.println("╔═════════════════════════════════════════════════════════╗");
        System.out.printf ("║             AVAILABLE COURSES (Page %d of %d)             ║%n", page, totalPages);
        System.out.println("╚═════════════════════════════════════════════════════════╝");
        // Print only the courses that belong on this page
        for (int i = start; i < end; i++) {
            Course c = COURSES[i];
            System.out.printf("%2d│ %s%n", c.number, c.name);   // %2d keeps the numbers aligned
        }
        System.out.println("═══════════════════════════════════════════════════════════");
        // Page 1 has no "Back" option
        if (page == 1) {
            System.out.println("                     [N] Next Page");
        } else {
            System.out.println("         [B] Back Page     │     [N] Next Page");
        }
        System.out.println("═══════════════════════════════════════════════════════════");
    }

    /** Reads and validates the user's sex input (Female/Male or F/M). */
    private static String readSex(Scanner scanner) {
        while (true) {   // repeat until a valid answer is returned
            System.out.print("Enter your sex (F or M): ");
            String input = scanner.nextLine().trim().toUpperCase();   // toUpperCase lets "f" and "F" both work
            
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

    /** Reads and validates the course selection number with pagination support. */
    private static int readCourseChoice(Scanner scanner) {
        int currentPage = 1;
        boolean redraw = true;   // only redraw the list when the page changes

        while (true) {
            if (redraw) {
                printCoursePage(currentPage);
                redraw = false;
            }

            System.out.println();
            if (currentPage == 1) {
                System.out.print("Enter the NUMBER of your target course (or 'N' for Next Page): ");
            } else {
                System.out.print("Enter the NUMBER of your target course (or 'N' for Next / 'B' for Back): ");
            }

            String input = scanner.nextLine().trim();

            // N = next page; the % 3 makes page 3 wrap around back to page 1
            if (input.equalsIgnoreCase("N") || input.equalsIgnoreCase("NEXT")) {
                currentPage = (currentPage % 3) + 1;
                clearScreen();
                redraw = true;
                continue;
            // B = previous page (not allowed on page 1)
            } else if (input.equalsIgnoreCase("B") || input.equalsIgnoreCase("BACK")) {
                if (currentPage > 1) {
                    currentPage--;
                    clearScreen();
                    redraw = true;
                    continue;
                } else {
                    System.out.println("Invalid option. You are already on the first page.");
                    continue;
                }
            }

            // Otherwise, the input must be a valid course number
            try {
                int choice = Integer.parseInt(input);   // throws an error if input isn't a number
                if (choice >= 1 && choice <= COURSES.length) {
                    return choice;   // valid: exit the loop and return the number
                } else {
                    System.out.println("Please enter a number between 1 and " + COURSES.length + ".");
                }
            } catch (NumberFormatException e) {
                // Input wasn't a number (and wasn't N or B)
                if (currentPage == 1) {
                    System.out.println("Invalid input. Please enter a valid course number or 'N' for Next Page.");
                } else {
                    System.out.println("Invalid input. Please enter a valid course number, 'N' for Next, or 'B' for Back.");
                }
            }
        }
    }

    /** Reads and validates a non-negative income value. */
    private static double readDouble(Scanner scanner, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().replace(",", "");   // allow "150,000" by removing commas
            try {
                value = Double.parseDouble(line);   // convert text to a number
                // Reject special values like NaN or Infinity
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    System.out.println("Invalid input. Please enter numbers only (e.g., 150000).");
                    continue;
                }
                // Income can't be below zero
                if (value < 0) {
                    System.out.println("Income cannot be negative. Please try again.");
                    continue;
                }
                break;   // valid input: leave the loop
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
                // Grade must be within the 0-100 range
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

    // SecureRandom gives unpredictable random numbers (better than plain Random for IDs)
    private static final SecureRandom RANDOM = new SecureRandom();

    /** Generates a random 8-character alphanumeric USER ID. */
    private static String generateUserId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";   // pool of allowed characters
        StringBuilder sb = new StringBuilder();
        // Pick 8 random characters from the pool and join them together
        for (int i = 0; i < 8; i++) {
            int index = RANDOM.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
