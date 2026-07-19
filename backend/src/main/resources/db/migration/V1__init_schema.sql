-- Create core tables for roles and permissions
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    failed_attempts INT DEFAULT 0,
    lock_time DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Refresh Tokens and OTPs
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE otps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    INDEX idx_otps_email (email)
);

-- Core school structures
CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    room_number VARCHAR(20),
    capacity INT NOT NULL,
    UNIQUE KEY uq_class_name_year (name, academic_year)
);

CREATE TABLE sections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    class_id BIGINT NOT NULL,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    UNIQUE KEY uq_section_class (name, class_id)
);

-- Teachers, Parents and Students Profile Tables
CREATE TABLE teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    photo_url VARCHAR(255),
    qualification VARCHAR(255) NOT NULL,
    experience INT NOT NULL, -- in years
    salary DECIMAL(10,2) NOT NULL,
    department VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_teachers_email (email)
);

-- Add class teacher to sections
ALTER TABLE sections ADD COLUMN class_teacher_id BIGINT;
ALTER TABLE sections ADD CONSTRAINT fk_sections_class_teacher FOREIGN KEY (class_teacher_id) REFERENCES teachers(id) ON DELETE SET NULL;

CREATE TABLE parents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    occupation VARCHAR(100),
    relation VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_parents_email (email)
);

CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    admission_number VARCHAR(50) NOT NULL UNIQUE,
    roll_number VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    gender VARCHAR(20) NOT NULL,
    blood_group VARCHAR(10),
    religion VARCHAR(50),
    nationality VARCHAR(50) DEFAULT 'Indian',
    category VARCHAR(50),
    photo_url VARCHAR(255),
    class_id BIGINT NOT NULL,
    section_id BIGINT,
    parent_id BIGINT,
    phone VARCHAR(20),
    email VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    medical_info TEXT,
    emergency_contact VARCHAR(100),
    academic_year VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (class_id) REFERENCES classes(id),
    FOREIGN KEY (section_id) REFERENCES sections(id),
    FOREIGN KEY (parent_id) REFERENCES parents(id) ON DELETE SET NULL,
    INDEX idx_students_adm (admission_number),
    INDEX idx_students_class (class_id),
    INDEX idx_students_status (status)
);

-- Subjects and Class Subjects Mapping
CREATE TABLE subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE,
    credits INT NOT NULL,
    department VARCHAR(100) NOT NULL
);

CREATE TABLE class_subjects (
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT,
    PRIMARY KEY (class_id, subject_id),
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE SET NULL
);

-- Attendance Table
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    status VARCHAR(20) NOT NULL, -- PRESENT, ABSENT, LATE, LEAVE
    remarks VARCHAR(255),
    student_id BIGINT NOT NULL,
    marked_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (marked_by) REFERENCES teachers(id) ON DELETE SET NULL,
    UNIQUE KEY uq_attendance_student_date (student_id, date),
    INDEX idx_attendance_date (date)
);

-- Marks / Grade Tables
CREATE TABLE marks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    exam_type VARCHAR(50) NOT NULL, -- INTERNAL, MID_TERM, QUARTERLY, HALF_YEARLY, ANNUAL, ASSIGNMENT, PROJECT, PRACTICAL
    marks_obtained DECIMAL(5,2) NOT NULL,
    max_marks DECIMAL(5,2) NOT NULL,
    grade VARCHAR(5),
    remarks VARCHAR(255),
    graded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (graded_by) REFERENCES teachers(id) ON DELETE SET NULL,
    UNIQUE KEY uq_marks_student_subject_exam (student_id, subject_id, exam_type)
);

-- Homework Module
CREATE TABLE homework (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    file_path VARCHAR(255),
    due_date DATE NOT NULL,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES teachers(id) ON DELETE SET NULL
);

CREATE TABLE homework_submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    homework_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    submission_text TEXT,
    file_path VARCHAR(255),
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'SUBMITTED', -- SUBMITTED, EVALUATED
    marks DECIMAL(5,2),
    feedback VARCHAR(255),
    evaluated_by BIGINT,
    FOREIGN KEY (homework_id) REFERENCES homework(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluated_by) REFERENCES teachers(id) ON DELETE SET NULL,
    UNIQUE KEY uq_submission_homework_student (homework_id, student_id)
);

-- Exams Schedules and Results
CREATE TABLE exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

CREATE TABLE exam_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    max_marks DECIMAL(5,2) DEFAULT 100.0,
    FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);

CREATE TABLE exam_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    exam_schedule_id BIGINT NOT NULL,
    marks_obtained DECIMAL(5,2) NOT NULL,
    remarks VARCHAR(255),
    status VARCHAR(20) DEFAULT 'PASS', -- PASS, FAIL, ABSENT
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (exam_schedule_id) REFERENCES exam_schedules(id) ON DELETE CASCADE,
    UNIQUE KEY uq_student_exam_schedule (student_id, exam_schedule_id)
);

-- Fees & Payments
CREATE TABLE fees_structure (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    fee_type VARCHAR(100) NOT NULL, -- TUITION, EXAM, LIBRARY, TRANSPORT, HOSTEL, MISC
    amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE
);

CREATE TABLE fee_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    fees_structure_id BIGINT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    discount DECIMAL(10,2) DEFAULT 0.00,
    fine DECIMAL(10,2) DEFAULT 0.00,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(50) NOT NULL, -- CASH, UPI, CARD, NET_BANKING
    transaction_id VARCHAR(100),
    status VARCHAR(50) NOT NULL, -- PAID, PARTIAL, UNPAID
    receipt_no VARCHAR(100) NOT NULL UNIQUE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (fees_structure_id) REFERENCES fees_structure(id)
);

-- Weekly Timetable
CREATE TABLE timetables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL, -- MONDAY, TUESDAY, etc.
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    INDEX idx_timetables_conflict (teacher_id, day_of_week, start_time, end_time),
    INDEX idx_timetables_room_conflict (room_number, day_of_week, start_time, end_time)
);

-- Teacher Leave Management
CREATE TABLE teacher_leaves (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    leave_type VARCHAR(50) NOT NULL, -- CASUAL, SICK, MATERNITY, PATERNITY, UNPAID
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    approved_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Library Module
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    isbn VARCHAR(50) NOT NULL UNIQUE,
    author VARCHAR(150) NOT NULL,
    publisher VARCHAR(150),
    subject_id BIGINT,
    quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE SET NULL
);

CREATE TABLE book_issues (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    student_id BIGINT,
    teacher_id BIGINT,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    status VARCHAR(50) DEFAULT 'ISSUED', -- ISSUED, RETURNED, OVERDUE
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE
);

-- Transport
CREATE TABLE routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    start_point VARCHAR(150) NOT NULL,
    end_point VARCHAR(150) NOT NULL,
    fare DECIMAL(10,2) NOT NULL
);

CREATE TABLE vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(50) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    driver_name VARCHAR(100) NOT NULL,
    driver_phone VARCHAR(20) NOT NULL,
    driver_license VARCHAR(50) NOT NULL
);

CREATE TABLE student_transport (
    student_id BIGINT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    pickup_point VARCHAR(150) NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Hostel
CREATE TABLE hostels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- BOYS, GIRLS
    address VARCHAR(255) NOT NULL,
    warden_name VARCHAR(100) NOT NULL,
    warden_phone VARCHAR(20) NOT NULL
);

CREATE TABLE hostel_rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hostel_id BIGINT NOT NULL,
    room_number VARCHAR(20) NOT NULL,
    capacity INT NOT NULL,
    occupied INT DEFAULT 0,
    fee_per_semester DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (hostel_id) REFERENCES hostels(id) ON DELETE CASCADE,
    UNIQUE KEY uq_hostel_room (hostel_id, room_number)
);

CREATE TABLE student_hostel (
    student_id BIGINT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    join_date DATE NOT NULL,
    leave_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES hostel_rooms(id) ON DELETE CASCADE
);

-- Inventory Asset Management
CREATE TABLE inventory_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    sku VARCHAR(50) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit VARCHAR(20) NOT NULL,
    location VARCHAR(100),
    status VARCHAR(50) NOT NULL, -- IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Finance and Expense Management
CREATE TABLE finance_ledgers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL, -- INCOME, EXPENSE
    category VARCHAR(100) NOT NULL, -- MOCK FEES, SALARY, UTILITIES, MAINTENANCE, PURCHASE, MISC
    amount DECIMAL(12,2) NOT NULL,
    transaction_date DATE NOT NULL,
    description VARCHAR(255),
    reference_no VARCHAR(100) UNIQUE,
    created_by BIGINT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Notice Board
CREATE TABLE notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    content TEXT NOT NULL,
    attachment_path VARCHAR(255),
    target_audience VARCHAR(50) NOT NULL, -- ALL, TEACHERS, STUDENTS, PARENTS
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Events & Calendar
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    type VARCHAR(50) DEFAULT 'EVENT', -- EVENT, HOLIDAY, EXAM_TERM
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit Logging
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- School Settings
CREATE TABLE school_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    school_name VARCHAR(100) NOT NULL DEFAULT 'Enterprise Academy',
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    logo_path VARCHAR(255),
    currency VARCHAR(10) DEFAULT 'INR',
    current_academic_year VARCHAR(20) DEFAULT '2026-2027'
);
