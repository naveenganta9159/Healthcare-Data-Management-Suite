<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Healthcare Patient Registration</title>
    <link rel="stylesheet" href="assets/css/style.css">
    <script defer src="assets/js/validation.js"></script>
</head>
<body>
<div class="container">
    <h1>Healthcare Patient Registration</h1>
    <p>Fill in details, attach reports/bills, and click "Generate & Save PDF".</p>
    <hr>

    <form action="export" method="post" enctype="multipart/form-data">
   <!-- Hospital Info -->
        <label>Hospital Name:</label>
        <input type="text" name="hospitalName" value="XYZ Multispeciality Hospital" required>

        <label>Report Title:</label>
        <input type="text" name="reportTitle" value="Patient Report" required>

        <!-- Patient & Doctor Info -->
        <label>Patient ID:</label>
        <input type="text" name="patientId" required>

        <label>Doctor Name:</label>
        <input type="text" name="doctorName" required>

        <label>Department:</label>
        <input type="text" name="department" required>

        <h2>Personal Details</h2>
        <label>First Name</label>
        <input type="text" name="firstName" required>

        <label>Last Name</label>
        <input type="text" name="lastName" required>

        <label>Date of Birth</label>
        <input type="date" name="dob" required>

        <label>Gender</label>
        <select name="gender" required>
            <option value="">Select</option><option>Male</option><option>Female</option><option>Other</option>
        </select>

        <label>Contact Number</label>
        <input type="tel" name="contact" required>

	<label>Alternative Contact Number</label>
        <input type="tel" name="alternativecontact" required>

        <label>Email</label>
        <input type="email" name="email" required>

        <label>Address</label>
        <textarea name="address" rows="2" required></textarea>

        <label>Aadhar Number</label>
        <input type="text" name="aadhar" required>

        <label>Blood Group</label>
        <select name="bloodGroup" required>
            <option value="">Select</option>
            <option>A+</option><option>A-</option><option>B+</option><option>B-</option>
            <option>O+</option><option>O-</option><option>AB+</option><option>AB-</option>
        </select>

        <label>Medical History</label>
        <textarea name="medicalHistory" rows="3"></textarea>

        <label>Current Medications</label>
        <textarea name="currentMedications" rows="2"></textarea>

        <label>Allergies</label>
        <textarea name="allergies" rows="2"></textarea>

        <label>Insurance Provider</label>
        <input type="text" name="insuranceProvider">

        <label>Policy Number</label>
        <input type="text" name="policyNumber">

        <h2>Visit & Billing</h2>
        <label>Visit Date</label>
        <input type="date" name="visitDate" required>

        <label>Consultation Type</label>
        <select name="consultation" required>
            <option value="">Select</option>
            <option>General</option><option>Specialist</option><option>Emergency</option><option>Follow-Up</option>
        </select>

        <label>Service Description</label>
        <textarea name="serviceDescription" rows="2"></textarea>

        <label>Tests Ordered</label>
        <textarea name="testsOrdered" rows="2"></textarea>

        <label>Test Results</label>
        <textarea name="testResults" rows="2"></textarea>

        <label>Medications Prescribed</label>
        <textarea name="medications" rows="2"></textarea>

        <div class="grid">
            <div>
                <label>Consultation Fee (₹)</label>
                <input type="number" step="0.01" name="consultationFee" id="consultationFee" oninput="calcTotal()" required>
            </div>
            <div>
                <label>Test Charges (₹)</label>
                <input type="number" step="0.01" name="testCharges" id="testCharges" oninput="calcTotal()">
            </div>
            <div>
                <label>Medicine Charges (₹)</label>
                <input type="number" step="0.01" name="medicineCharges" id="medicineCharges" oninput="calcTotal()">
            </div>
            <div>
                <label>Other Charges (₹)</label>
                <input type="number" step="0.01" name="otherCharges" id="otherCharges" oninput="calcTotal()">
            </div>
            <div>
                <label>Total Amount (₹)</label>
                <input type="number" step="0.01" name="totalAmount" id="totalAmount" readonly required>
            </div>
            <div>
                <label>Payment Status</label>
                <select name="paymentStatus" required>
                    <option value="">Select</option><option>Paid</option><option>Pending</option><option>Partially Paid</option>
                </select>
            </div>
        </div>

        <label>Notes</label>
        <textarea name="notes" rows="3"></textarea>

        <h2>Attachments</h2>
        <p>You can attach images (embedded in the report) and PDFs (merged after the report).</p>
        <input type="file" name="attachments" multiple>

        <div class="actions">
            <button type="submit">Generate & Save PDF</button>
            <a class="secondary" href="reports">View Previous Reports</a>
        </div>
    </form>
</div>

<script>
function v(id){ return parseFloat(document.getElementById(id)?.value||0) || 0; }
function calcTotal(){
  const total = v('consultationFee') + v('testCharges') + v('medicineCharges') + v('otherCharges');
  document.getElementById('totalAmount').value = total.toFixed(2);
}
</script>
</body>
</html>

