-- Seed Roles
INSERT INTO roles (name, description) VALUES
('ROLE_SUPER_ADMIN', 'Super Administrator with full database and system access'),
('ROLE_ADMIN', 'School Administrator managing academics, finances, and users'),
('ROLE_TEACHER', 'Academic teacher managing attendance, marks, homework, and leaves'),
('ROLE_STUDENT', 'Student viewing grades, timetable, homework, and paying fees'),
('ROLE_PARENT', 'Parent tracking children attendance, homework, grades, and fees');

-- Seed Permissions
INSERT INTO permissions (name, description) VALUES
('READ_STUDENTS', 'View student details and profiles'),
('WRITE_STUDENTS', 'Add and update student profiles'),
('DELETE_STUDENTS', 'Soft-delete student profiles'),
('READ_TEACHERS', 'View teacher profiles'),
('WRITE_TEACHERS', 'Add and update teacher profiles'),
('DELETE_TEACHERS', 'Soft-delete teacher profiles'),
('READ_PARENTS', 'View parent profiles'),
('WRITE_PARENTS', 'Add and update parent profiles'),
('DELETE_PARENTS', 'Soft-delete parent profiles'),
('READ_ATTENDANCE', 'View attendance reports'),
('WRITE_ATTENDANCE', 'Record daily attendance'),
('READ_MARKS', 'View student exam marks'),
('WRITE_MARKS', 'Enter and edit exam marks'),
('READ_FEES', 'View fee structures and payment status'),
('WRITE_FEES', 'Submit fee payments and modify fee structures'),
('READ_LEAVES', 'View teacher leave requests'),
('WRITE_LEAVES', 'Apply for and approve leave requests'),
('WRITE_TIMETABLE', 'Configure weekly class schedules'),
('READ_INVENTORY', 'View inventory and school asset levels'),
('WRITE_INVENTORY', 'Update inventory and issue stock items'),
('READ_FINANCE', 'View finance ledgers and income/expense dashboards'),
('WRITE_FINANCE', 'Record incoming fees and outgoing expenses'),
('READ_AUDIT', 'View system audit logs'),
('WRITE_SETTINGS', 'Modify school branding and global configurations');

-- Map Permissions to Roles
-- Super Admin: All permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 1, id FROM permissions;

-- Admin: Most permissions except database administration / audit
INSERT INTO role_permissions (role_id, permission_id)
SELECT 2, id FROM permissions WHERE name NOT IN ('READ_AUDIT', 'DELETE_STUDENTS', 'DELETE_TEACHERS');

-- Teacher: Attendance, Marks, Homework, Leaves
INSERT INTO role_permissions (role_id, permission_id)
SELECT 3, id FROM permissions WHERE name IN ('READ_STUDENTS', 'READ_ATTENDANCE', 'WRITE_ATTENDANCE', 'READ_MARKS', 'WRITE_MARKS', 'WRITE_LEAVES');

-- Student: Read own data
INSERT INTO role_permissions (role_id, permission_id)
SELECT 4, id FROM permissions WHERE name IN ('READ_STUDENTS', 'READ_ATTENDANCE', 'READ_MARKS', 'READ_FEES');

-- Parent: Read child data
INSERT INTO role_permissions (role_id, permission_id)
SELECT 5, id FROM permissions WHERE name IN ('READ_STUDENTS', 'READ_ATTENDANCE', 'READ_MARKS', 'READ_FEES');

-- Seed Users (Password is BCrypt hash of 'password': $2a$10$8.UnVuG9HHgffUDAlk8qCOuy5fKbC2XM9W.8maV79GQC.P3t/.rKy)
INSERT INTO users (username, email, password, enabled, account_non_locked) VALUES
('superadmin', 'superadmin@school.com', '$2a$10$8.UnVuG9HHgffUDAlk8qCOuy5fKbC2XM9W.8maV79GQC.P3t/.rKy', TRUE, TRUE),
('admin', 'admin@school.com', '$2a$10$8.UnVuG9HHgffUDAlk8qCOuy5fKbC2XM9W.8maV79GQC.P3t/.rKy', TRUE, TRUE),
('teacher_smith', 'smith@school.com', '$2a$10$8.UnVuG9HHgffUDAlk8qCOuy5fKbC2XM9W.8maV79GQC.P3t/.rKy', TRUE, TRUE),
('student_john', 'john.doe@school.com', '$2a$10$8.UnVuG9HHgffUDAlk8qCOuy5fKbC2XM9W.8maV79GQC.P3t/.rKy', TRUE, TRUE),
('parent_doe', 'parent.doe@school.com', '$2a$10$8.UnVuG9HHgffUDAlk8qCOuy5fKbC2XM9W.8maV79GQC.P3t/.rKy', TRUE, TRUE);

-- Map Users to Roles
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- superadmin -> Super Admin
(2, 2), -- admin -> Admin
(3, 3), -- teacher_smith -> Teacher
(4, 4), -- student_john -> Student
(5, 5); -- parent_doe -> Parent

-- Seed School Settings
INSERT INTO school_settings (school_name, address, phone, email, currency, current_academic_year) VALUES
('Enterprise Academy', '123 Tech Avenue, Silicon Valley', '+1 555-0199', 'info@enterpriseacademy.edu', 'USD', '2026-2027');

-- Seed Classes and Sections
INSERT INTO classes (name, academic_year, room_number, capacity) VALUES
('Grade 10', '2026-2027', 'Room 101', 30),
('Grade 11', '2026-2027', 'Room 102', 30),
('Grade 12', '2026-2027', 'Room 103', 30);

-- Seed Teachers Profile
INSERT INTO teachers (user_id, name, email, phone, qualification, experience, salary, department) VALUES
(3, 'Mr. Alan Smith', 'smith@school.com', '+1 555-0210', 'M.Sc. in Mathematics', 8, 4500.00, 'Mathematics');

-- Seed Sections
INSERT INTO sections (name, class_id, class_teacher_id) VALUES
('Section A', 1, 1), -- Grade 10 - Section A taught by Alan Smith
('Section B', 1, NULL),
('Section A', 2, NULL);

-- Seed Parents Profile
INSERT INTO parents (user_id, name, email, phone, occupation, relation, address) VALUES
(5, 'Robert Doe', 'parent.doe@school.com', '+1 555-0320', 'Software Architect', 'FATHER', '456 Oak Lane, California');

-- Seed Students Profile
INSERT INTO students (user_id, admission_number, roll_number, name, dob, gender, blood_group, religion, category, class_id, section_id, parent_id, phone, email, academic_year) VALUES
(4, 'ADM2026001', '10', 'John Doe', '2011-05-14', 'MALE', 'O+', 'Christianity', 'GENERAL', 1, 1, 1, '+1 555-0321', 'john.doe@school.com', '2026-2027');

-- Seed Subjects
INSERT INTO subjects (name, code, credits, department) VALUES
('Mathematics', 'MATH101', 4, 'Mathematics'),
('Science', 'SCI101', 4, 'Science'),
('English Literature', 'ENG101', 3, 'Humanities'),
('Computer Science', 'CS101', 4, 'Computer Science');

-- Map Subjects to Classes & Teachers
INSERT INTO class_subjects (class_id, subject_id, teacher_id) VALUES
(1, 1, 1), -- Grade 10 Math taught by Alan Smith
(1, 2, NULL),
(1, 4, 1); -- Grade 10 Computer Science taught by Alan Smith

-- Seed a Notice
INSERT INTO notices (title, content, target_audience, created_by) VALUES
('Welcome to Academic Year 2026-2027', 'We are excited to welcome all staff, students, and parents to the new academic year. Standard classes start next Monday at 8:00 AM.', 'ALL', 1);

-- Seed an Event
INSERT INTO events (title, description, start_time, end_time, type) VALUES
('Parent Teacher Orientation', 'Introduction of teachers, courses syllabus, and grading rules.', '2026-08-01 09:00:00', '2026-08-01 13:00:00', 'EVENT'),
('Independence Day Holiday', 'School holiday in observation of Independence Day.', '2026-07-04 00:00:00', '2026-07-04 23:59:59', 'HOLIDAY');

-- Seed Inventory
INSERT INTO inventory_items (name, sku, category, quantity, unit, location, status) VALUES
('Whiteboard Markers', 'MKT-WB-01', 'Stationery', 120, 'pcs', 'Main Store Room', 'IN_STOCK'),
('Microscope Model 2', 'LAB-MIC-02', 'Science Lab', 15, 'pcs', 'Lab A', 'IN_STOCK'),
('Classroom Desks', 'FUR-DSK-10', 'Furniture', 250, 'pcs', 'Classrooms & Hallways', 'IN_STOCK'),
('Standard A4 Printing Paper', 'STA-A4-05', 'Office Supplies', 5, 'boxes', 'Administrative Office', 'LOW_STOCK');

-- Seed Finance Ledger
INSERT INTO finance_ledgers (type, category, amount, transaction_date, description, reference_no, created_by) VALUES
('INCOME', 'MOCK FEES', 15000.00, '2026-07-10', 'Collected tuition fees from new admissions', 'TXN-7489201', 1),
('EXPENSE', 'SALARY', 4500.00, '2026-07-15', 'Monthly salary for Mr. Alan Smith', 'TXN-9021893', 1),
('EXPENSE', 'MAINTENANCE', 350.00, '2026-07-18', 'Air conditioner repair in Admin Room', 'TXN-1002341', 1);
