---
name: java-mcp-debugger
description: Uses the Java MCP Debugger to attach to JVM processes over JDWP, manage breakpoints, step through code, inspect variables and stack traces, and evaluate expressions at runtime to debug Java applications and tests.
---

# Java MCP Debugger Skill

This skill provides step-by-step guidance for driving live Java debugging sessions via the **Java MCP Debugger** (`mazen-aissa.java-mcp-debugger`), allowing runtime state inspection, hypothesis verification, and interactive bug investigation.

---

## 1. Core Architecture & Prerequisites

- **MCP Endpoint**: The extension exposes an HTTP Streamable MCP server at `http://127.0.0.1:18990/mcp`.
- **JDWP Transport**: The target JVM must run with Java Debug Wire Protocol (JDWP) enabled (standard port: `5005`).
- **Configuration**:
  - VS Code MCP declared in `.vscode/mcp.json`:
    ```json
    {
      "servers": {
        "java-debugger": {
          "type": "http",
          "url": "http://127.0.0.1:18990/mcp"
        }
      }
    }
    ```
  - Cursor MCP declared in `.cursor/mcp.json` (`"mcpServers": { "java-debugger": { "url": "http://127.0.0.1:18990/mcp" } }`).

---

## 2. Launching Target JVM with JDWP

Before attaching the debugger, start the target Java application or test suite with JDWP arguments:

### A. Running Maven Tests (Isolated Debugging)
To pause JVM on startup until debugger attaches (`suspend=y`):
```bash
mvn test -Dtest=NomeDoTeste -Dmaven.surefire.debug="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005"
```
Or for non-suspending test runs (`suspend=n`):
```bash
mvn test -Dmaven.surefire.debug="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### B. Running Spring Boot Applications
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### C. Standalone JAR Execution
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 -jar target/app.jar
```

---

## 3. Systematic Debugging Workflow for the Agent

When debugging an issue with `java-mcp-debugger`:

1. **Verify JDWP Process**:
   - Ensure the JVM process is active and listening on the designated port (e.g. `5005`).

2. **Attach Debugger**:
   - Invoke the attach MCP tool to connect to `localhost:5005`.

3. **Set Breakpoints Cirurgicamente**:
   - Identify candidate source files and line numbers where the faulty logic, null pointer, or unexpected state occurs.
   - Set breakpoints before triggering execution.

4. **Resume / Drive Execution**:
   - If JVM started with `suspend=y`, continue execution to hit the first breakpoint.
   - Send requests / triggers to the app or let test execution advance.

5. **Inspect Runtime State on Breakpoint**:
   - **Stack Trace**: Examine call hierarchy and active thread frames.
   - **Variables**: Inspect local variables, method arguments, and object fields (`this`, DTOs, XML nodes, response payloads).
   - **Evaluate Expressions**: Test conditions, invoke getters, or verify hypotheses directly in the paused context.

6. **Step Execution**:
   - Use **Step Over** (`next`) to advance line by line.
   - Use **Step In** (`stepIn`) to enter suspicious method calls.
   - Use **Step Out** (`stepOut`) to return to the calling function.

7. **Synthesize & Fix**:
   - Once the exact root cause (mutation, null value, serialization error, unexpected branch) is identified, detach session and apply the code fix.

---

## 4. Best Practices & Troubleshooting

- **Port in use / Connection Refused**:
  - Verify if another debug session is already attached to `5005`.
  - Check whether the JVM started before attaching.
- **Breakpoint Not Hitting**:
  - Verify if the class was compiled with debug symbols (`-g` in Maven compiler plugin, default in most builds).
  - Ensure the file path and line number match the executed bytecode.
- **Timeouts & Suspended Threads**:
  - Avoid keeping HTTP requests suspended longer than external socket timeouts when debugging network-sensitive operations.
