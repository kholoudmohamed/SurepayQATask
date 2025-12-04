# SurePay QA Task - API Test Automation

## 🎯 Overview

A REST API test automation task covering some workflows of JSONPlaceholder Blog API.

## 🚀 Quick Start

### Prerequisites
- **Java 21+** (OpenJDK or Oracle)
- **Maven 3.6+**
- **Git** (for cloning)

### Running Tests (Any Platform)

```bash
# Clone repository
git clone https://github.com/kholoudmohamed/SurepayQATask.git
cd SurepayQATask

# Run all tests
mvn clean test

# Run with specific environment
mvn clean test -Dtest.environment=prod
mvn clean test -Dtest.environment=test
```

After running tests, reports are generated automatically:

```
target/
├── extent-reports/
│   └── ExtentReport.html          # Detailed HTML report with charts
└── surefire-reports/
    ├── index.html                 # TestNG summary
    └── TEST-*.xml                 # JUnit XML format
```

## 🧪 Test Scenarios

📋 **[Complete Test Documentation](TestDocuments/TestDocumentation.md)** - Comprehensive test report with coverage analysis, performance results, and defect tracking

## 🛠 Framework Architecture

### Technology Stack
- **Java 21**: Latest LTS with enhanced performance
- **TestNG 7.8.0**: Flexible test execution and parallel support
- **REST Assured 5.3.2**: Fluent API testing with built-in validation
- **Extent Reports 5.1.2**: Rich HTML reports with charts and analytics
- **Maven**: Cross-platform dependency management and build automation

### Project Structure
```
src/test/
├── java/com/surepay/
│   ├── framework/
│   │   ├── config/          # Environment and test configuration management
│   │   ├── Models/          # Data models for API entities (User, Post, Comment)
│   │   ├── Services/        # API service classes for endpoints
│   │   └── reporting/       # Custom test report listeners
│   └── tests/              # Test classes and test suites
└── resources/
    ├── config/             # Environment configuration files (.properties)
    └── testng.xml          # Test suite configuration and report settings
```

## 🌍 Environment Support

### Configuration Files
```
src/test/resources/config/
├── default.properties    # Default settings
├── test.properties      # Local testing
└── prod.properties      # Production-like testing
```

## 📈 Continuous Integration

### CircleCI Pipeline
🔗 **Pipeline**: Available at CircleCI (link shared separately for assessment)

> **Note**: CircleCI pipeline access may be restricted. For assessment purposes:
> - Pipeline screenshots can be provided upon request
> - Test reports are available as downloadable artifacts
> - Local execution produces identical results

### Pipeline Features
- **Automatic Triggers**: Every push and pull request
- **Cross-Platform Testing**: Docker-based execution ensures consistency
- **Artifact Storage**: Reports preserved for 30 days
- **Test Result Integration**: Native CircleCI test dashboard

### Viewing Test Results
1. **Local Execution**: Run `mvn clean test` → Check `target/extent-reports/ExtentReport.html`
2. **CircleCI Artifacts**: Download reports from pipeline artifacts
3. **Test Dashboard**: View individual test results in CircleCI Tests tab

## 🔧 Advanced Usage

### Code Formatting
```bash
# Check code formatting
mvn spotless:check

# Apply automatic formatting
mvn spotless:apply

# Formatting includes:
# - Eclipse Java formatter for consistent styling
# - Import ordering and organization  
# - Unused import removal
# - Proper indentation and spacing
```

### Running Specific Tests
```bash
# Single test class
mvn test -Dtest=BlogWorkflowTests

# Specific test method
mvn test -Dtest=BlogWorkflowTests#testCompleteWorkflow

# Test groups (if using TestNG groups)
mvn test -Dgroups=smoke,regression,edge-case
```

### Custom Configuration
```bash

# Enable debug logging
mvn test -Dmaven.surefire.debug=true

# Parallel execution
mvn test -Dthreadcount=4
```