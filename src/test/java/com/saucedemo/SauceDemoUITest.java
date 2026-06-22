package com.saucedemo;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lab 9 - Kiểm thử tự động giao diện (UI E2E Testing)
 * Trang web: SauceDemo (https://www.saucedemo.com/)
 * Công cụ: Selenium WebDriver 4.x + JUnit 5
 *
 * Bao gồm 3 Test Cases:
 *   1. Đăng nhập thành công (Login)
 *   2. Thêm sản phẩm vào giỏ hàng (Add to Cart)
 *   3. Đăng xuất khỏi hệ thống (Logout)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SauceDemo - Kiểm thử giao diện tự động")
public class SauceDemoUITest {

    private WebDriver driver;
    private WebDriverWait wait;

    // Thông tin đăng nhập hợp lệ
    private static final String BASE_URL = "https://www.saucedemo.com/";
    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";

    /**
     * Khởi tạo ChromeDriver và thiết lập WebDriverWait trước mỗi test case.
     */
    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Tùy chọn: chạy ở chế độ maximize để dễ quan sát
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        // Thiết lập Explicit Wait tối đa 10 giây
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Đóng trình duyệt sau mỗi test case.
     */
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Phương thức hỗ trợ: Thực hiện đăng nhập với username và password.
     */
    private void performLogin(String username, String password) {
        driver.get(BASE_URL);

        // Nhập username
        WebElement usernameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))
        );
        usernameField.clear();
        usernameField.sendKeys(username);

        // Nhập password
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.clear();
        passwordField.sendKeys(password);

        // Click nút Login
        driver.findElement(By.id("login-button")).click();
    }

    // ==================== TEST CASE 1: ĐĂNG NHẬP THÀNH CÔNG ====================

    /**
     * Test Case 1: Đăng nhập thành công (Login)
     *
     * Mô tả: Kiểm tra chức năng đăng nhập với tài khoản hợp lệ.
     * Input: Username = "standard_user", Password = "secret_sauce"
     * Kỳ vọng:
     *   - URL chuyển sang https://www.saucedemo.com/inventory.html
     *   - Trang hiển thị tiêu đề "Products"
     */
    @Test
    @Order(1)
    @DisplayName("TC01 - Đăng nhập thành công với tài khoản hợp lệ")
    void testLoginSuccess() {
        // Bước 1: Thực hiện đăng nhập
        performLogin(VALID_USERNAME, VALID_PASSWORD);

        // Bước 2: Kiểm tra URL sau khi đăng nhập
        wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/inventory.html"));
        String currentUrl = driver.getCurrentUrl();
        assertEquals("https://www.saucedemo.com/inventory.html", currentUrl,
                "URL sau đăng nhập phải là trang inventory");

        // Bước 3: Kiểm tra tiêu đề "Products" hiển thị trên trang
        WebElement productsTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='title']")
                )
        );
        assertEquals("Products", productsTitle.getText(),
                "Tiêu đề trang phải là 'Products'");
    }

    // ==================== TEST CASE 2: THÊM SẢN PHẨM VÀO GIỎ HÀNG ====================

    /**
     * Test Case 2: Thêm sản phẩm vào giỏ hàng (Add to Cart)
     *
     * Mô tả: Kiểm tra việc thêm sản phẩm "Sauce Labs Backpack" vào giỏ hàng.
     * Các bước: Đăng nhập -> Click "Add to cart" ở sản phẩm đầu tiên
     * Kỳ vọng: Biểu tượng giỏ hàng hiển thị badge số "1"
     */
    @Test
    @Order(2)
    @DisplayName("TC02 - Thêm sản phẩm Sauce Labs Backpack vào giỏ hàng")
    void testAddToCart() {
        // Bước 1: Đăng nhập vào hệ thống
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        // Bước 2: Click nút "Add to cart" của sản phẩm Sauce Labs Backpack
        WebElement addToCartButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("add-to-cart-sauce-labs-backpack")
                )
        );
        addToCartButton.click();

        // Bước 3: Kiểm tra badge trên biểu tượng giỏ hàng hiển thị số "1"
        WebElement cartBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("[data-test='shopping-cart-badge']")
                )
        );
        assertEquals("1", cartBadge.getText(),
                "Badge giỏ hàng phải hiển thị số '1' sau khi thêm 1 sản phẩm");
    }

    // ==================== TEST CASE 3: ĐĂNG XUẤT KHỎI HỆ THỐNG ====================

    /**
     * Test Case 3: Đăng xuất khỏi hệ thống (Logout)
     *
     * Mô tả: Đảm bảo người dùng có thể đăng xuất an toàn.
     * Các bước: Đăng nhập -> Click Menu -> Chờ menu mở -> Click "Logout"
     * Kỳ vọng: Chuyển hướng về trang đăng nhập (https://www.saucedemo.com/)
     */
    @Test
    @Order(3)
    @DisplayName("TC03 - Đăng xuất khỏi hệ thống thành công")
    void testLogout() {
        // Bước 1: Đăng nhập vào hệ thống
        performLogin(VALID_USERNAME, VALID_PASSWORD);
        wait.until(ExpectedConditions.urlContains("inventory.html"));

        // Bước 2: Click vào nút Menu (biểu tượng 3 gạch ngang - hamburger menu)
        WebElement menuButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn"))
        );
        menuButton.click();

        // Bước 3: Chờ sidebar menu mở hoàn toàn (animation slide-in)
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".bm-menu-wrap[aria-hidden='false']")
                )
        );

        // Bước 4: Chờ nút Logout clickable rồi click
        WebElement logoutLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))
        );
        logoutLink.click();

        // Bước 5: Kiểm tra đã quay về trang đăng nhập
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.startsWith("https://www.saucedemo.com"),
                "Sau khi logout, URL phải quay về trang đăng nhập. Hiện tại: " + currentUrl);
    }
}
