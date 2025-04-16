# Latte: Lightweight Aliasing Tracking for Java

Latte is a lightweight static analysis tool for tracking aliasing in Java programs. This tool allows you to annotate your programs with permissions where aliases are tracked to prevent bugs related to unexpected object references and side effects.

For more details, check our [research paper](https://arxiv.org/pdf/2309.05637).
Test our VSCode Plugin in the Codespaces of the project [latte-vscode](https://github.com/CatarinaGamboa/latte-vscode).

## Overview

Latte enables developers to identify and track aliasing relationships in their Java code using a permission-based type system. The tool statically verifies aliasing properties to help prevent common concurrency and memory management bugs.

## Latte Annotations System

Latte uses annotations to specify the permissions of fields and parameters to track their uniqueness properties and the aliases that are created throughout the program:

- For parameters:
  - `@Free` - For parameters that are globally unique. When a caller passes an argument to a `@Free` parameter, they cannot observe that value again.
  - `@Borrowed` - For parameters that are unique in the callee's scope but may be stored elsewhere on the heap or stack.
- For fields:
  - `@Unique` - For fields that cannot be aliased with other fields.
- For both:
  - `@Shared` - For parameters or fields that can be shared, so they have no uniqueness guarantees.

Local variables are not annotated and start with a default annotation that allows them to take the assignment's permissions.

## Project Structure

```
latte-umbrella/
├── src/
│   └── main/
│       └── java/
│           ├── latte/
│           │   └── latte_umbrella/
│           │       └── App.java  # Main application entry point
│           └── examples/         # Test examples for the analysis
│   └── test/
│       └── java/
│           ├── AppTest.java # Creating JUnit tests
│       └── examples/ # Java classes to be used in the JUnit tests

```

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Prerequisites

- Java 11 or higher
- Maven

### Installation

Clone the repository and build with Maven:

```bash
git clone https://github.com/CatarinaGamboa/latte.git
cd latte/latte-umbrella
mvn clean install
```

To use Latte in your Java projects, you need to:
1. Add the latte.jar to your project dependencies
2. Import the specifications in your files (e.g., `import specification.Free`)


### Usage

Run the tool against your Java code by executing the main application:

```bash
java -cp target/latte-umbrella-1.0.0.jar latte.latte_umbrella.App [options] <file-to-analyze>
```

## Running Examples

The project includes example files to test the analysis capabilities:

```bash
# Run the analysis on a specific example
java -cp target/latte-umbrella-1.0.0.jar latte.latte_umbrella.App src/main/java/examples/Example1.java
```

## Example

Here is an example of Java classes using the Latte annotations:

```java
class Node {
    @Unique Object value;   // Field value is Unique
    @Unique Node next;      // Field next is Unique


    public Node (@Free Object value, @Free Node next) {
        // We can assign @Free objects to Unique fields given that they have no aliases
        this.value = value; 
        this.next = next;
    }
}

public class MyStack {

    @Unique Node root;          // Field root is Unique		
    
    public MyStack(@Free Node root) {
        this.root = root;       // Since root is @Free we can assign it to root:@Unique		
    }
    
    void push(@Free Object value) {	
        Node r;                 // Local variables start with a default annotation that allows
        Node n;                 // them to take the assignment's permissions
        
        r = this.root; 			// r is an alias to this.root with permission @Unique
        this.root = null; 		// Nullify this.root so there is only one pointer to the 
                                // value of the root, no other aliases
        n = new Node(value, r); // Create new root with values that have no aliases. The constructors  
                                // always return an new object that is @Free 
        this.root = n; 			// Replace root with a new value that is @Free and so can be assigned 
                                // to an @Unique field
    }
}
```

## Testing

Tests can be run using Maven:

```bash
mvn test
```

You can also test the tool directly on the example files:

```bash
java -cp target/latte-umbrella-1.0.0.jar latte.latte_umbrella.App src/main/java/examples/MyStack.java
```

## Contributing

We welcome contributions to Latte! Here's how to get started:

### Development Workflow

1. **Create a new branch for your feature:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes and commit them:**
   ```bash
   git add .
   git commit -m "Description of your changes"
   ```

3. **Set the upstream branch and push your changes:**
   ```bash
   git push --set-upstream origin feature/your-feature-name
   ```

4. **Create a Pull Request:**
   - Go to the repository on GitHub
   - Click on "Pull Requests" > "New Pull Request"
   - Select your branch
   - Fill in the PR template with details about your changes
   - Request a review from a team member

5. **Address review feedback** and update your PR as needed

### Code Standards

- Follow Java code conventions
- Write unit tests for new functionality
- Update documentation for any changes

## Known Limitations

The tool is still under active development, and some features are limited:

- Does not support if statements without else branches
- Limited verification of aliases related to while loops
- Limited support for complex collections and generics

## Future Work

We are continuously improving Latte to support more complex Java patterns and enhance the aliasing tracking capabilities.

## Contributing Issues

If you encounter any problems while using Latte, please add an Issue to the repository with:
- A description of the problem
- Code sample that demonstrates the issue
- Expected vs. actual behavior

## License

[Your license here]

## Contact

[Your contact information here]