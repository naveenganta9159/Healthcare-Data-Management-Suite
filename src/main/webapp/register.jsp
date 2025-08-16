<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Healthcare Patient Registration</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1, h2 { color: #2c3e50; }
        form { max-width: 800px; margin: auto; }
        label { display: block; margin-top: 10px; font-weight: bold; }
        input, select, textarea { width: 100%; padding: 8px; margin-top: 5px; }
        .section { border: 1px solid #ddd; padding: 15px; margin-top: 20px; border-radius: 5px; }
        .submit-btn { margin-top: 20px; padding: 12px; background: #27ae60; color: #fff; border: none; cursor: pointer; width: 100%; font-size: 16px; }
        .submit-btn:hover { background: #219150; }
    </style>
</head>
<body>
    <h1>Healthcare Patient Registration</h1>
    <p>Fill in details, attach reports/bills, and click "Generate & Save PDF".</p>

    <form action="export" method="post" enctype="multipart/form-data">

        <!-- Personal Details -->
        <div class="section">
            <h2>Personal Details</h2>
            <label>First Name</label>
            <input type="text" name="firstName" required>

            <label>Last Name</label>
            <input type="text" name="lastName" required>

            <label>Date of Birth</label>
            <input type="date" name="dob" required>

            <label>Gender</label>
            <select name="gender" required>
                <option value="">-- Select --</option>
                <option>Male</option>
                <option>Female</option>
                <option>Other</option>
            </select>

            <label>Contact Number</label>
            <input type="text" name="contact" required>

            <label>Alternative Contact Number</label>
            <input type="text" name="altContact">

            <label>Email</label>
            <input type="email" name="email">

            <label>Address</label>
            <textarea name="address" rows="3"></textarea>

            <label>Aadhar Number</label>
            <input type="text" name="aadhar">

            <label>Blood Group</label>
            <input type="text" name="bloodGroup">
        </div>

        <!-- Medical Info -->
        <div class="section">
            <h2>Medical Information</h2>
            <label>Medical History</label>
            <textarea name="medicalHistory" rows="2"></textarea>

            <label>Current Medications</label>
            <textarea name="medications" rows="2"></textarea>

            <label>Allergies</label>
            <textarea name="allergies" rows="2"></textarea>
        </div>

        <!-- Insurance -->
        <div class="section">
            <h2>Insurance</h2>
            <label>Insurance Provider</label>
            <input type="text" name="insurance">

            <label>Policy Number</label>
            <input type="text" name="policyNumber">
        </div>

        <!-- Visit & Billing -->
        <div class="section">
            <h2>Visit & Billing</h2>
            <label>Visit Date</label>
            <input type="date" name="visitDate">

            <label>Consultation Type</label>
            <input type="text" name="consultationType">

            <label>Service Description</label>
            <input type="text" name="serviceDescription">

            <label>Tests Ordered</label>
            <textarea name="testsOrdered" rows="2"></textarea>

            <label>Test Results</label>
            <textarea name="testResults" rows="2"></textarea>

            <label>Medications Prescribed</label>
            <textarea name="medicationsPrescribed" rows="2"></textarea>

            <label>Consultation Fee (₹)</label>
            <input type="number" name="consultationFee">

            <label>Test Charges (₹)</label>
            <input type="number" name="testCharges">

            <label>Medicine Charges (₹)</label>
            <input type="number" name="medicineCharges">

            <label>Other Charges (₹)</label>
            <input type="number" name="otherCharges">

            <label>Total Amount (₹)</label>
            <input type="number" name="totalAmount">

            <label>Payment Status</label>
            <select name="paymentStatus">
                <option value="">-- Select --</option>
                <option>Paid</option>
                <option>Unpaid</option>
                <option>Pending</option>
            </select>
        </div>

        <!-- Notes & Attachments -->
        <div class="section">
            <h2>Notes & Attachments</h2>
            <label>Notes</label>
            <textarea name="notes" rows="3"></textarea>

            <label>Attachments</label>
            <input type="file" name="attachments" multiple>
        </div>

        <button type="submit" class="submit-btn">Generate & Save PDF</button>
    </form>
</body>
</html>

