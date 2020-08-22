import java.util.List;

public class Parser {

    private UI ui;
    private TaskList taskList;
    private DukeSaver saver;

    public Parser(UI ui, TaskList taskList, DukeSaver saver) {
        this.ui = ui;
        this.taskList = taskList;
        this.saver = saver;
    }

    public void handleResponse(String response) throws DukeException {
        String[] parsedResponse = response.split(" ", 2);
        Command command;
        try {
            command = Command.valueOf(parsedResponse[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            command = Command.INVALID;
        }
        String rest = parsedResponse.length == 1 ? null : parsedResponse[1];
        switch (command) {
        case BYE:
            saver.saveData(taskList);
            ui.exit();
        case LIST:
            handleList();
            break;
        case DONE:
            handleDone(rest);
            break;
        case TODO:
            handleTodo(rest);
            break;
        case DEADLINE:
            handleDeadline(rest);
            break;
        case EVENT:
            handleEvent(rest);
            break;
        case DELETE:
            handleDelete(rest);
            break;
        case FIND:
            handleFind(rest);
            break;
        case INVALID:
            throw new DukeException("Unrecognized command!");
        }
    }

    private void handleList() {
        if (taskList.isEmpty()) {
            ui.print("You have no tasks!");
            return;
        }

        StringBuilder sb = new StringBuilder("Here are the tasks in your list:\n");
        for (int i = 0; i < taskList.size(); ++i) {
            sb.append((i + 1) + "." + taskList.get(i) + "\n");
        }
        sb.setLength(sb.length() - 1);
        ui.print(sb.toString());
    }

    private void handleDone(String rest) throws DukeException {
        if (rest == null) {
            throw new DukeException("Specify a task!");
        }
        int taskDone;
        try {
            taskDone = Integer.parseInt(rest) - 1;
        } catch (NumberFormatException ex) {
            throw new DukeException("Specify a valid task number!");
        }
        if (taskDone < 0 || taskDone >= taskList.size()) {
            throw new DukeException("No such task!");
        }
        taskList.markTaskDone(taskDone);
        ui.print("Nice! I've marked this task as done:\n" + taskList.get(taskDone));
    }

    private void handleTodo(String rest) throws DukeException {
        if (rest == null) {
            throw new DukeException("Description of a todo cannot be empty!");
        }
        Todo todo = new Todo(rest);
        taskList.addTask(todo);
        ui.print("Got it. I've added this task:\n" + todo + "\n" + taskList.taskSizeString());
    }

    private void handleDeadline(String rest) throws DukeException {
        if (rest == null) {
            throw new DukeException("Description of a deadline cannot be empty!");
        }
        String[] deadlineParsed = rest.split("/");
        if (deadlineParsed.length == 1) {
            throw new DukeException("Prefix the keyword 'by' with a forward slash!");
        }
        String deadlineName = deadlineParsed[0].trim();
        String[] byParsed = deadlineParsed[1].split(" ", 2);
        if (byParsed.length == 1) {
            throw new DukeException("Deadline due time cannot be empty!");
        }
        String by = byParsed[1];
        Deadline deadline = new Deadline(deadlineName, by);
        taskList.addTask(deadline);
        ui.print("Got it. I've added this task:\n" + deadline + "\n" + taskList.taskSizeString());
    }

    private void handleEvent(String rest) throws DukeException {
        if (rest == null) {
            throw new DukeException("Description of an event cannot be empty!");
        }
        String[] eventParsed = rest.split("/");
        if (eventParsed.length == 1) {
            throw new DukeException("Prefix the keyword 'at' with a forward slash!");
        }
        String eventName = eventParsed[0].trim();
        String[] atParsed = eventParsed[1].split(" ", 2);
        if (atParsed.length == 1) {
            throw new DukeException("Event time cannot be empty!");
        }
        String at = atParsed[1];
        Event event = new Event(eventName, at);
        taskList.addTask(event);
        ui.print("Got it. I've added this task:\n" + event + "\n" + taskList.taskSizeString());
    }

    private void handleDelete(String rest) throws DukeException {
        if (rest == null) {
            throw new DukeException("Specify a task!");
        }
        int deleteIndex;
        try {
            deleteIndex = Integer.parseInt(rest) - 1;
        } catch (NumberFormatException ex) {
            throw new DukeException("Specify a valid task number!");
        }
        if (deleteIndex < 0 || deleteIndex >= taskList.size()) {
            throw new DukeException("No such task!");
        }
        Task taskToDelete = taskList.get(deleteIndex);
        taskList.deleteTask(deleteIndex);
        ui.print("Noted. I've removed this task:\n" + taskToDelete + "\n" + taskList.taskSizeString());
    }

    /**
     * Handles the "find" command.
     *
     * @param rest Remaining string after the "find" command, which is query term.
     * @throws DukeException Thrown when no search term is given.
     */
    private void handleFind(String rest) throws DukeException {
        if (rest == null) {
            throw new DukeException("Specify a search term!");
        }

        List<Task> matches = taskList.search(rest);

        if (matches.isEmpty()) {
            ui.print("No matching task.");
            return;
        }

        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:\n");

        for (int i = 0; i < matches.size(); ++i) {
            sb.append((i + 1) + "." + matches.get(i) + "\n");
        }
        sb.setLength(sb.length() - 1);
        ui.print(sb.toString());
    }
}
