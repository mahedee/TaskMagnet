## TaskMagnet Work Breakdown Structure (WBS) - Draft

---

### A. Authentication & Authorization

1. Create Spring Security config class for JWT-based auth
2. Implement user registration API
3. Implement user login API (returns JWT)
4. Add password encryption using BCrypt
5. Create frontend registration form
6. Create frontend login form
7. Store JWT in localStorage after login
8. Create Axios auth interceptor to send JWT
9. Create logout button and clear token
10. Create middleware for protected routes in React

---

### B. Project Management

11. Create Project entity with fields (id, name, description, createdAt)
12. Create ProjectRepository interface
13. Create ProjectController with basic CRUD endpoints
14. Create AddProjectForm in React
15. Create ProjectList component
16. Implement EditProjectForm component
17. Implement DeleteProject button
18. Style project form and list using Tailwind

---

### C. Issue/Task Management

19. Create Task entity (id, title, description, priority, status, dueDate, projectId)
20. Create TaskRepository interface
21. Create TaskController with CRUD endpoints
22. Create endpoint to get tasks by project ID
23. Create AddTaskForm component
24. Create TaskList component
25. Create EditTaskForm component
26. Create DeleteTask button
27. Add dropdown for task priority and status
28. Style task form and list with Tailwind

---

### D. Boards

29. Create entity: BoardColumn (id, title, projectId, order)
30. Create Board entity (projectId, column list)
31. API to get board columns by project
32. API to update column order (drag/drop)
33. Create KanbanBoard component
34. Create draggable TaskCard component
35. Implement drag-and-drop with dnd-kit
36. Create AddColumnForm
37. Style Kanban board layout

---

### E. Sprint Planning

38. Create Sprint entity (id, name, startDate, endDate, projectId)
39. Create SprintRepository and SprintController
40. Add tasks to a sprint API
41. Create SprintList component
42. Create AddSprintForm component
43. Add sprint dropdown in AddTaskForm

---

### F. Epics

44. Create Epic entity and controller
45. Link Epic to multiple tasks
46. Create EpicList component
47. Add Epic dropdown in task form

---

### G. User Management

48. Create User entity with roles
49. Admin API to fetch users
50. Create UserList admin page
51. Add user role dropdown

---

### H. Workflow Management

52. Create Status entity
53. Create TransitionRules table
54. Create UI to define workflow per project

---

### I. Search & Filters

55. Add global search bar in header
56. Backend API for task search
57. Add filters for status/assignee in TaskList

---

### J. Notifications

58. Create Notification entity
59. Add notification generation on task update
60. Create NotificationBell component

---

### K. Comments

61. Create Comment entity
62. Add comment form in TaskDetail view
63. Display comment thread

---

### L. Time Tracking

64. Add estimatedTime and loggedTime to Task
65. Create TimeLog entity and API
66. Add Log Time form to TaskDetail

---

### M. Reports

67. Create basic API to return issue counts by status
68. Create PieChart component for status overview
69. Create BurndownChart component

---

### N. Custom Fields

70. Create CustomField entity
71. Allow adding fields to a project
72. Render custom fields dynamically in forms

---

### O. Labels

73. Create Label entity
74. Add labels to tasks
75. Add Label filter in TaskList

---

### P. Permissions

76. Create ProjectRole table
77. Restrict endpoint access by role
78. Add permission manager UI

---

### Q. Activity Logs

79. Create ActivityLog entity
80. Log task creation/edit/delete
81. Display activity log in TaskDetail

---

### R. Components/Modules

82. Create Component entity
83. Add component selector in task form
84. Filter tasks by component

---

### S. Releases

85. Create Version/Release entity
86. Assign tasks to version
87. Generate release notes from completed tasks

---

### T. Dark Mode

88. Add dark mode toggle button
89. Apply Tailwind dark class strategy

---

### U. Mobile Responsiveness

90. Test and fix layout for mobile in Kanban board
91. Make task modals mobile-friendly

---

### V. API Integration

92. Expose public REST APIs with Swagger UI
93. Add basic webhook support (e.g. on task status change)

---

### W. Import/Export

94. Create CSV import API for tasks
95. Create export to CSV button in TaskList

---

### X. Keyboard Shortcuts

96. Implement `n` to open Add Task modal
97. Implement `/` to focus global search

---

### Y. AI-Assisted Features (Optional)

98. Add OpenAI integration to suggest due dates
99. Summarize long task descriptions using GPT

---
