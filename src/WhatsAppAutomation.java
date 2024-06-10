import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Scanner;
import java.util.regex.Pattern;

public class WhatsAppAutomation {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the contact names or phone numbers (comma separated):");
        String contactsOrNumbersInput = scanner.nextLine().trim();
        String[] contactsOrNumbers = contactsOrNumbersInput.split(",");


        System.out.println("Enter the message to send:");
        String message = scanner.nextLine().trim();

        WebDriver driver = null;
        try {

            driver = new ChromeDriver();

            driver.get("https://web.whatsapp.com");
            driver.manage().window().maximize();

            // Wait for the user to scan the QR code and for WhatsApp Web to load
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(2));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@contenteditable='true' and @data-tab='3']")));

            // Loop through each contact or number and send the message
            for (String contactOrNumber : contactsOrNumbers) {
                contactOrNumber = contactOrNumber.trim();  // Trim any extra spaces
                if (isPhoneNumber(contactOrNumber)) {
                    sendMessageToNumber(driver, wait, contactOrNumber, message);
                } else {
                    sendMessageToContact(driver, wait, contactOrNumber, message);
                }
                // Add a delay between messages to avoid triggering anti-bot measures
                Thread.sleep(2000);
            }

            System.out.println("Messages sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void sendMessageToContact(WebDriver driver, WebDriverWait wait, String contactName, String message) {
        try {
            // Search for the chat by contact name or group name
            WebElement searchBox = driver.findElement(By.xpath("//div[@contenteditable='true' and @data-tab='3']"));
            searchBox.click();
            //Thread.sleep(1000);
            searchBox.clear(); // Clear previous input
            searchBox.sendKeys(contactName);

            // Wait for the contact to appear and click on it
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + contactName.toLowerCase() + "')]")));
            WebElement contact = driver.findElement(By.xpath("//span[contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + contactName.toLowerCase() + "')]"));
            //Thread.sleep(1000);
            contact.click();

            // Locate the message input box
            WebElement messageBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@contenteditable='true' and @data-tab='10']")));

            // Type and send the message
            messageBox.sendKeys(message);
            //Thread.sleep(1000);
            messageBox.sendKeys("\n"); // Press Enter to send the message

            System.out.println("Message sent to " + contactName);
        } catch (Exception e) {
            System.out.println("Failed to send message to " + contactName);
            e.printStackTrace();
        }
    }

    private static void sendMessageToNumber(WebDriver driver, WebDriverWait wait, String phoneNumber, String message) {
        try {
            // Format phone number to the required URL format
            String url = "https://web.whatsapp.com/send?phone=" + phoneNumber;
            driver.get(url);

            // Check if the number is on WhatsApp by looking for the message box
            WebElement messageBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@contenteditable='true' and @data-tab='10']")));

            // If message box is found, the number is on WhatsApp
            messageBox.sendKeys(message);
          //  Thread.sleep(1000);
            messageBox.sendKeys("\n"); // Press Enter to send the message

            System.out.println("Message sent to " + phoneNumber);
        } catch (Exception e) {
            System.out.println("Number is not on WhatsApp: " + phoneNumber);
            dismissAlertIfPresent(driver, wait);
        }
    }

    private static boolean isPhoneNumber(String input) {
        // Improved regex to check if the input is a valid phone number
        // Allows digits, spaces, dashes, and parentheses, and may start with a plus sign for country code
        return Pattern.matches("^\\+?[\\d\\s\\-()]+$", input) && input.replaceAll("[^\\d]", "").length() <= 12;
    }
    private static void dismissAlertIfPresent(WebDriver driver, WebDriverWait wait) {
        try {
            // Wait for the alert dialog to be present
            WebElement alertDialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@role='alertdialog']")));

            // Locate and click the "OK" button within the alert dialog
            WebElement okButton = alertDialog.findElement(By.xpath(".//button[contains(@class, 'btn-primary') or @data-testid='popup-controls-ok']"));
            Thread.sleep(1000);
            okButton.click();

            System.out.println("Dismissed alert dialog.");
        } catch (Exception e) {
            System.out.println("No alert dialog present or unable to dismiss.");
        }
    }
}
