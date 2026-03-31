-- Initialize Roles
-- (Assuming Role IDs: 1: ROLE_STUDENT, 2: ROLE_EMPLOYER)
-- Note: Entities will be mapped to these in Java

-- Mock Jobs across Three Cities

-- BANGALORE (Technological Hub)
-- Lat: 12.9716, Lng: 77.5946
INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Java Backend Intern', 'Join our core platform team to build cloud-native services using Spring Boot.', 'Skyline Tech', '$25/hr', 'INTERNSHIP', 'Java, Spring Boot, MySQL', 12.9716, 77.5946, NOW(), 'ACTIVE', true, false, true);

INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Wait-staff (Part-time)', 'Looking for dynamic students for evening shifts at our premium lounge.', 'Blue Lotus', '$18/hr', 'PART_TIME', 'Communication, Hospitality', 12.9600, 77.5800, NOW(), 'ACTIVE', true, true, false);

INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Content Writer (Gig)', 'Weekly assignments for our global tech blog. High-quality English required.', 'Aether Content', '$150/article', 'GIG', 'Content Writing, SEO', 13.0100, 77.6200, NOW(), 'ACTIVE', true, false, false);

-- MUMBAI (Financial & Entertainment)
-- Lat: 19.0760, Lng: 72.8777
INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Data Entry Associate', 'Evening shifts for processing financial documents. Accuracy is key.', 'FinEdge Corp', '$20/hr', 'PART_TIME', 'Excel, Data Entry', 19.0760, 72.8777, NOW(), 'ACTIVE', true, false, false);

INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Photography Intern', 'Support our fashion shoots at Marine Drive. Must have basic DSLR knowledge.', 'Urban Clicks', '$300/month', 'INTERNSHIP', 'Photography, Photoshop', 18.9400, 72.8200, NOW(), 'ACTIVE', true, false, false);

-- DELHI (Capital & Service)
-- Lat: 28.6139, Lng: 77.2090
INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Customer Support Night Shift', 'Remote support for international clients. Perfect for students.', 'GlobalConnect', '$22/hr', 'PART_TIME', 'English, Remote Support', 28.6139, 77.2090, NOW(), 'ACTIVE', true, false, false);

INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Event Coordinator Help', 'Occasional gig work for major expos at Pragati Maidan.', 'Expos Galore', '$50/day', 'GIG', 'Management, On-field Support', 28.6200, 77.2400, NOW(), 'ACTIVE', true, false, false);

-- FAR AWAY (For Testing Dynamic Range)
INSERT INTO jobs (title, description, company, salary, type, skills_required, lat, lng, created_at, status, is_approved, is_urgent, is_boosted) 
VALUES ('Remote Tutor (Advanced)', 'Teaching coding to students across Asia. Totally remote.', 'ByteAcademy', '$40/hr', 'GIG', 'Teaching, Coding', 10.0000, 75.0000, NOW(), 'ACTIVE', true, false, false);
