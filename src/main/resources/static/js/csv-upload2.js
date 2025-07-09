// Mobile menu toggle functionality
const menuToggle = document.getElementById("menuToggle")
const sidebar = document.querySelector(".sidebar")

if (menuToggle) {
    menuToggle.addEventListener("click", () => {
        sidebar.classList.toggle("open")
    })
}

// Close sidebar when clicking outside on mobile
document.addEventListener("click", (e) => {
    if (window.innerWidth <= 1024) {
        if (!sidebar.contains(e.target) && sidebar.classList.contains("open")) {
            sidebar.classList.remove("open")
        }
    }
})

// DOM Elements
const uploadZone = document.getElementById("uploadZone")
const fileInput = document.getElementById("csvFile")
const fileInfo = document.getElementById("fileInfo")
const fileName = document.getElementById("fileName")
const fileSize = document.getElementById("fileSize")
const uploadBtn = document.getElementById("uploadBtn")
const errorMessage = document.getElementById("errorMessage")
const successMessage = document.getElementById("successMessage")
const progressFill = document.getElementById("progressFill")
const uploadForm = document.getElementById("uploadForm")

// Configuration
const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB in bytes
const ALLOWED_EXTENSIONS = [".csv"]

// Initialize event listeners when DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
    initializeEventListeners()
})

/**
 * Initialize all event listeners
 */
function initializeEventListeners() {
    // Drag and drop functionality
    uploadZone.addEventListener("dragover", handleDragOver)
    uploadZone.addEventListener("dragleave", handleDragLeave)
    uploadZone.addEventListener("drop", handleDrop)

    // File input change
    fileInput.addEventListener("change", handleFileSelect)

    // Form submission
    uploadForm.addEventListener("submit", handleFormSubmit)
}

/**
 * Handle drag over event
 */
function handleDragOver(e) {
    e.preventDefault()
    uploadZone.classList.add("dragover")
}

/**
 * Handle drag leave event
 */
function handleDragLeave() {
    uploadZone.classList.remove("dragover")
}

/**
 * Handle drop event
 */
function handleDrop(e) {
    e.preventDefault()
    uploadZone.classList.remove("dragover")

    const files = e.dataTransfer.files
    if (files.length > 0) {
        fileInput.files = files
        handleFileSelect()
    }
}

/**
 * Handle file selection (both drag-drop and file input)
 */
function handleFileSelect() {
    const file = fileInput.files[0]
    hideMessages()

    if (file) {
        const validation = validateFile(file)

        if (!validation.isValid) {
            showError(validation.message)
            return
        }

        displayFileInfo(file)
        enableUploadButton()
        simulateProgress()
    }
}

/**
 * Validate selected file
 */
function validateFile(file) {
    // Check file extension
    const fileExtension = "." + file.name.split(".").pop().toLowerCase()
    if (!ALLOWED_EXTENSIONS.includes(fileExtension)) {
        return {
            isValid: false,
            message: "Please select a CSV file (.csv)",
        }
    }

    // Check file size
    if (file.size > MAX_FILE_SIZE) {
        return {
            isValid: false,
            message: "File size must be less than 10MB",
        }
    }

    return { isValid: true }
}

/**
 * Display file information
 */
function displayFileInfo(file) {
    fileName.textContent = file.name
    fileSize.textContent = formatFileSize(file.size)
    fileInfo.classList.add("show")
}

/**
 * Format file size in human readable format
 */
function formatFileSize(bytes) {
    if (bytes === 0) return "0 Bytes"

    const k = 1024
    const sizes = ["Bytes", "KB", "MB", "GB"]
    const i = Math.floor(Math.log(bytes) / Math.log(k))

    return Number.parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i]
}

/**
 * Simulate upload progress (for visual feedback)
 */
function simulateProgress() {
    let progress = 0
    const interval = setInterval(() => {
        progress += Math.random() * 30
        if (progress >= 100) {
            progress = 100
            clearInterval(interval)
        }
        updateProgress(progress)
    }, 200)
}

/**
 * Show error message
 */
function showError(message) {
    errorMessage.textContent = message
    errorMessage.style.display = "block"
    resetFileInput()
}

/**
 * Show success message
 */
function showSuccess(message) {
    successMessage.textContent = message
    successMessage.style.display = "block"
}

/**
 * Hide all messages
 */
function hideMessages() {
    errorMessage.style.display = "none"
    successMessage.style.display = "none"
}

/**
 * Enable upload button
 */
function enableUploadButton() {
    uploadBtn.disabled = false
}

/**
 * Disable upload button
 */
function disableUploadButton() {
    uploadBtn.disabled = true
}

/**
 * Reset form to initial state
 */
function resetForm() {
    resetFileInput()
    hideMessages()
    progressFill.style.width = "0%"
}

/**
 * Reset file input and related UI elements
 */
function resetFileInput() {
    fileInput.value = ""
    fileInfo.classList.remove("show")
    disableUploadButton()
    progressFill.style.width = "0%"
}

/**
 * Handle form submission
 */
function handleFormSubmit(e) {
    // Show loading state
    showLoadingState()
    disableUploadButton()
}

/**
 * Show loading state on upload button
 */
function showLoadingState() {
    uploadBtn.innerHTML = `
        <svg class="animate-spin" width="20" height="20" fill="none" viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" opacity="0.25"/>
            <path fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
        </svg>
        Uploading...
    `
}

/**
 * Reset upload button to original state
 */
function resetUploadButton() {
    uploadBtn.innerHTML = `
        <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                  d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"/>
        </svg>
        Upload CSV
    `
}


/**
 * Update progress bar
 */
function updateProgress(progress) {
    progressFill.style.width = progress + "%"
    const progressText = document.getElementById("progressText")
    if (progressText) {
        progressText.textContent = Math.round(progress) + "%"
    }
}

// Global functions (accessible from HTML onclick attributes)
window.resetForm = resetForm

// Utility functions for potential future use
const CSVUpload = {
    validateFile,
    formatFileSize,
    showError,
    showSuccess,
    resetForm,
    hideMessages,
}

// Export for potential module use
if (typeof module !== "undefined" && module.exports) {
    module.exports = CSVUpload
}
