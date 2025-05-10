# Financial Tracker Application

A full-featured Spring Boot application for tracking incomes, expenses, budgets, and financial goals. This project demonstrates several key **structural design patterns** in a real-world context.

---

## 🚀 How to Run the Application

### **Prerequisites**
- Java 17+
- Maven 3.6+
- MySQL (or update `application.properties` for your DB)

### **Steps**
1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd Financial-Tracker-MVC-workingCode
   ```
2. **Configure the database:**
   - Edit `src/main/resources/application.properties` with your DB credentials.
3. **Build the project:**
   ```bash
   mvn clean install -DskipTests
   ```
4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
5. **Open your browser:**
   - Visit [http://localhost:8080/](http://localhost:8080/)
   - Login or register (if registration is enabled)

---

## 🏗️ Creational Design Pattern Used

### 1. **Factory Pattern**
**Purpose:** Encapsulates object creation logic, allowing for flexible and consistent instantiation of complex objects.

**Where:** `TransactionFactory.java`

**How to View:**
- Used throughout the app to create `Expense`, `Income`, and `Budget` objects.
- **Files:** `TransactionFactory.java`, `ExpenseController.java`, `IncomeController.java`, `BudgetController.java`

**Code Example:**
```java
// In ExpenseController, IncomeController, etc.
Expense newExpense = TransactionFactory.createExpense(
    user, amount, category, description, date, recurring
);
Income newIncome = TransactionFactory.createIncome(
    user, amount, source, category, date, recurring
);
Budget newBudget = TransactionFactory.createBudget(
    user, category, limitAmount, startDate, endDate
);
```

### 2. **Prototype Pattern**
**Purpose:** Create new objects by copying (cloning) existing ones, useful for recurring or templated data.

**Where:** `prototype/RecurringTransaction.java`, `prototype/RecurringExpense.java`, `prototype/RecurringIncome.java`

**How to View:**
- Used for recurring transactions (expenses/incomes) to efficiently create new instances based on a template.
- **Files:** `RecurringTransaction.java`, `RecurringExpense.java`, `RecurringIncome.java`

**Code Example:**
```java
// Cloning a recurring expense
RecurringExpense recurring = new RecurringExpense(existingExpense);
Expense nextMonth = recurring.clone(); // New Expense with same fields
```

### 3. **Singleton Pattern**
**Purpose:** Ensure a class has only one instance and provide a global point of access to it.

**Where:** Spring-managed beans by default (e.g., services annotated with `@Component`, `@Service`, `@Repository`)

**How to View:**
- All Spring beans are singletons unless explicitly marked otherwise.
- **Files:** Any class with `@Component`, `@Service`, or `@Repository` annotation (e.g., `FinancialSummaryFacade.java`, `CurrencyConverterProxy.java`)

**Code Example:**
```java
@Component
public class FinancialSummaryFacade {
    // This bean is a singleton by default in Spring
}
```

---

## 🏗️ Structural Design Patterns Used

### 1. **Facade Pattern**
**Purpose:** Simplifies complex subsystem interactions by providing a unified interface.

**Where:** `FinancialSummaryFacade.java`

**How to View:**
- Used in `FinancialGoalController` to aggregate data from incomes, expenses, budgets, and goals.
- **Endpoint:** `/goals/summary` (returns a JSON summary for the logged-in user)

  ![image](https://github.com/user-attachments/assets/75522eb7-c629-4da8-8c26-554478e09e13)

**Code Example:**
```java
// In FinancialGoalController
@Autowired
private FinancialSummaryFacade financialSummaryFacade;

@GetMapping("/goals/summary")
@ResponseBody
public FinancialSummary getSummary(Authentication auth) {
    User user = userRepository.findByEmail(auth.getName()).orElseThrow();
    return financialSummaryFacade.getUserFinancialSummary(user.getId());
}
```

### 2. **Adapter Pattern**
**Purpose:** Allows incompatible interfaces to work together.

**Where:**
- `ThirdPartyCurrencyService.java` (simulated external API)
- `CurrencyConverter.java` (target interface)
- `ThirdPartyCurrencyAdapter.java` (adapter)

**How to View:**
- Used for currency conversion in the dashboard UI.
- **UI:** Currency Converter card on the dashboard (`/dashboard`)
- **Endpoint:** `/goals/convert-currency?amount=1000&from=INR&to=USD`

**Code Example:**
```java
// Interface
public interface CurrencyConverter {
    double convert(String fromCurrency, String toCurrency, double amount);
}

// Adapter
@Component
public class ThirdPartyCurrencyAdapter implements CurrencyConverter {
    private final ThirdPartyCurrencyService thirdPartyService = new ThirdPartyCurrencyService();
    public double convert(String from, String to, double amount) {
        return thirdPartyService.convert(from, to, amount);
    }
}
```

### 3. **Flyweight Pattern**
**Purpose:** Reduces memory usage by sharing common objects (e.g., enums) instead of creating many duplicates.

**Where:** `ExpenseCategoryFlyweightFactory.java`

**How to View:**
- Used when creating or updating expenses to ensure all categories are shared instances.
- **Files:** `ExpenseController.java`, `TransactionFactory.java`

**Code Example:**
```java
// In ExpenseController
Expense.ExpenseCategory flyweightCategory = ExpenseCategoryFlyweightFactory.getCategory(expense.getCategory().name());
Expense newExpense = TransactionFactory.createExpense(
    user, expense.getAmount(), flyweightCategory, ...
);
```

### 4. **Proxy Pattern**
**Purpose:** Adds extra functionality (like caching, logging, security) to an object without changing its interface.

**Where:** `CurrencyConverterProxy.java`

**How to View:**
- All currency conversions now go through this proxy for caching and logging.
- **UI:** Currency Converter card on the dashboard
- **Endpoint:** `/goals/convert-currency`
- **Logging:** Check your server logs for messages like:
  - `[PROXY] Cache hit for conversion: INR-USD-1000.0`
  - `[PROXY] Cache miss for conversion: INR-USD-1000.0. Delegating to real converter.`

**Code Example:**
```java
@Component
public class CurrencyConverterProxy implements CurrencyConverter {
    private final CurrencyConverter realConverter;
    private final Map<String, Double> cache = new HashMap<>();
    public double convert(String from, String to, double amount) {
        String key = from + "-" + to + "-" + amount;
        if (cache.containsKey(key)) {
            // log: cache hit
            return cache.get(key);
        }
        // log: cache miss
        double result = realConverter.convert(from, to, amount);
        cache.put(key, result);
        return result;
    }
}
```

---

## 🖥️ Where to See Each Pattern in Action

| Pattern   | File(s)                                   | How to Access/View                                      |
|-----------|-------------------------------------------|--------------------------------------------------------|
| Factory   | `TransactionFactory.java`                 | Create/Edit Expense, Income, Budget                    |
| Prototype | `RecurringExpense.java`, `RecurringIncome.java` | Used for recurring transactions (cloning)         |
| Singleton | `FinancialSummaryFacade.java`, `CurrencyConverterProxy.java` | All Spring beans by default (global instance) |
| Facade    | `FinancialSummaryFacade.java`             | `/goals/summary` (returns summary JSON)                |
| Adapter   | `ThirdPartyCurrencyAdapter.java`          | Dashboard → Currency Converter card                    |
| Flyweight | `ExpenseCategoryFlyweightFactory.java`    | Create/Edit Expense (category always shared instance)  |
| Proxy     | `CurrencyConverterProxy.java`             | Dashboard → Currency Converter card, server logs       |
{{ ... }}
- `src/main/java/com/financialtracker/prototype/RecurringExpense.java`
- `src/main/java/com/financialtracker/prototype/RecurringIncome.java`
{{ ... }}


![image](https://github.com/user-attachments/assets/3a0a1333-f8a5-42e8-9f53-fb9500772d7d)
![image](https://github.com/user-attachments/assets/9cd0e246-c75e-43e5-972f-ada5f8d658ff)



