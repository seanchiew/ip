# Orion User Guide

![Orion Screenshot](Ui.png)

## About Orion

Orion is a lightweight task tracker that helps you manage **todos**, **deadlines**, and **events** from a simple command interface.  
It supports searching, marking tasks as done/undone, deleting tasks, and **prevents accidental duplicate tasks** from being added.

---

## Quick start

1. Ensure you have **Java 17+** installed.
2. From the project root, run:

```bash
./gradlew run
```

3. Orion will print a welcome message and wait for your command.

---

## Command format

- Commands are **case-sensitive** (use lowercase as shown).
- Task numbers are **1-based** (i.e., the first task is `1`).
- Date format: `yyyy-MM-dd`
- Time format (optional): either `HHmm` (e.g., `1800`) or `HH:mm` (e.g., `18:00`)

---

## Features

### Supported commands

Orion accepts these commands:

- `todo`, `deadline`, `event`
- `list`, `find`
- `mark`, `unmark`, `delete`
- `bye`

(If you enter an unknown command, Orion will tell you what’s supported.)

---

### Adding a todo

Adds a simple task with a description.

**Format:** `todo <description>`

**Example:** `todo read CS2103T notes`

**Expected output:**
```text
    _______________________________________________________
    Got it. I've added this task:
      [T][ ] read CS2103T notes
    Now you have 1 tasks in the list.
    _______________________________________________________
```

---

### Adding a deadline

Adds a task that is due by a date (optionally with a time).

**Format:** `deadline <description> /by yyyy-MM-dd [HHmm|HH:mm]`

**Examples:**
- `deadline submit iP /by 2026-02-20`
- `deadline submit iP /by 2026-02-20 2359`
- `deadline submit iP /by 2026-02-20 23:59`

**Expected output (example):**
```text
    _______________________________________________________
    Got it. I've added this task:
      [D][ ] submit iP (by: Feb 20 2026 23:59)
    Now you have 2 tasks in the list.
    _______________________________________________________
```

---

### Adding an event

Adds a task that happens over a time period (start → end). Times are optional.

**Format:**  
`event <description> /from yyyy-MM-dd [HHmm|HH:mm] /to yyyy-MM-dd [HHmm|HH:mm]`

**Examples:**
- `event internship /from 2026-05-01 /to 2026-08-15`
- `event meeting /from 2026-02-10 1400 /to 2026-02-10 1500`
- `event meeting /from 2026-02-10 14:00 /to 2026-02-10 15:00`

**Expected output (example):**
```text
    _______________________________________________________
    Got it. I've added this task:
      [E][ ] meeting (from: Feb 10 2026 14:00 to: Feb 10 2026 15:00)
    Now you have 3 tasks in the list.
    _______________________________________________________
```

---

### Listing all tasks

**Format:** `list`

**Expected output (example):**
```text
    _______________________________________________________
    Here are the tasks in your list:
    1. [T][ ] read CS2103T notes
    2. [D][ ] submit iP (by: Feb 20 2026 23:59)
    3. [E][ ] meeting (from: Feb 10 2026 14:00 to: Feb 10 2026 15:00)
    _______________________________________________________
```

---

### Marking a task as done

**Format:** `mark <taskNumber>`

**Example:** `mark 2`

**Expected output (example):**
```text
    _______________________________________________________
    Nice! I've marked this task as done:
      [D][X] submit iP (by: Feb 20 2026 23:59)
    _______________________________________________________
```

---

### Unmarking a task

**Format:** `unmark <taskNumber>`

**Example:** `unmark 2`

**Expected output (example):**
```text
    _______________________________________________________
    OK, I've marked this task as not done yet:
      [D][ ] submit iP (by: Feb 20 2026 23:59)
    _______________________________________________________
```

---

### Deleting a task

**Format:** `delete <taskNumber>`

**Example:** `delete 1`

**Expected output (example):**
```text
    _______________________________________________________
    Noted. I've removed this task:
      [T][ ] read CS2103T notes
    Now you have 2 tasks in the list.
    _______________________________________________________
```

---

### Finding tasks by keyword

Finds tasks whose descriptions contain the given keyword (case-insensitive).

**Format:** `find <keyword>`

**Example:** `find submit`

**Expected output (example):**
```text
    _______________________________________________________
    Here are the matching tasks in your list:
    1. [D][ ] submit iP (by: Feb 20 2026 23:59)
    _______________________________________________________
```

---

### Duplicate task detection

When adding `todo`, `deadline`, or `event`, Orion checks if the new task is a **duplicate** of an existing one.

- Duplicate detection ignores completion status.
- Descriptions are normalized (extra spaces/casing differences won’t bypass detection).
- For `deadline` and `event`, the date/time fields must also match.

If a duplicate is detected, Orion will **not** add it.

**Example:** (trying to add the same todo again)  
`todo read   CS2103T  notes`

**Expected output (example):**
```text
    _______________________________________________________
    That task already exists in your list (not added):
      1. [T][ ] read CS2103T notes
    _______________________________________________________
```

---

### Exiting the program

**Format:** `bye`

**Expected output:**
```text
    _______________________________________________________
    Bye. Hope to see you again soon!
    _______________________________________________________
```

---

## Data storage

Orion saves tasks to a local text file:

- Default location: `data/orion.txt`
- You can override it by running with:

```bash
./gradlew run -Dorion.dataFile=path/to/yourfile.txt
```

> Note: Editing the save file manually may corrupt it and cause load errors.

---

## FAQ

**Q: Why does Orion reject my deadline/event date/time?**  
A: Ensure dates are `yyyy-MM-dd`. Times must be `HHmm` (e.g., `0900`) or `HH:mm` (e.g., `09:00`).

**Q: Why does `mark`/`delete` say the task number is invalid?**  
A: Task numbers are 1-based and must be within the current list size (use `list` to check).

<!-- As part of the A-AiAssisted increment, ChatGPT was used to help polish parts of this user guide for clarity. -->
