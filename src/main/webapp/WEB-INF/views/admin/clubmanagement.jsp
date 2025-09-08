<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Club Management - Admin Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <link href="<c:url value='/resources/css/main.css' />" rel="stylesheet">
</head>
<body>
    <jsp:include page="../layout/header.jsp" />
    
    <div class="d-flex">
        <!-- Left Sidebar -->
        <div class="bg-dark text-white position-fixed" style="width: 300px; min-height: 100vh; left: 0; top: 0; z-index: 1000;">
            <div class="p-3">
                <h5 class="text-white mb-4">
                    <i class="bi bi-trophy me-2"></i>ClubPoints Admin
                </h5>
                <p class="text-muted small mb-4">Administration Dashboard</p>
                
                <nav class="nav flex-column">
                    <a class="nav-link text-white-50 mb-2" href="${pageContext.request.contextPath}/admin/clubs">
                        <i class="bi bi-trophy me-2"></i>Club Dashboard
                    </a>
                    <a class="nav-link text-white mb-2 active" href="${pageContext.request.contextPath}/admin/clubmanagement">
                        <i class="bi bi-gear me-2"></i>Club Management
                    </a>
                    <a class="nav-link text-white-50 mb-2" href="${pageContext.request.contextPath}/admin/activitymanagement">
                        <i class="bi bi-calendar me-2"></i>Activity Management
                    </a>
                    <a class="nav-link text-white-50 mb-2" href="${pageContext.request.contextPath}/admin/studentmonitoring">
                        <i class="bi bi-mortarboard me-2"></i>Student Monitoring
                    </a>
                </nav>
            </div>
        </div>
        
     <!-- Main Content -->
     <div class="flex-grow-1" style="margin-left: 300px; padding-left: 1px;">
        <div class="container-fluid py-4">
                <!-- Header -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h2 class="mb-1"><i class="bi bi-gear text-primary me-2"></i>Club Management</h2>
                                <p class="text-muted mb-0">Advanced club administration and settings</p>
                            </div>
                            <div>
                              <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-2"></i>Back to Dashboard
                        </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Search and Add Section -->
                <div class="row mb-4">
                    <div class="col-12">
                        <div class="card border-0 shadow-sm">
                            <div class="card-body">
                                <div class="d-flex justify-content-between align-items-center">
                                    <div class="d-flex align-items-center">
                                        <div class="input-group me-3" style="width: 300px;">
                                            <span class="input-group-text bg-white border-end-0">
                                                <i class="bi bi-search text-muted"></i>
                                            </span>
                                            <input type="text" class="form-control border-start-0" placeholder="Search clubs..." id="clubSearchInput">
                                        </div>
                                    </div>
                                    <div>
                                        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addClubModal">
                                            <i class="bi bi-plus-circle me-2"></i>+ Add New Club
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Clubs Table -->
                <div class="row">
                    <div class="col-12">
                        <div class="card border-0 shadow-sm">
                            <div class="card-header bg-white border-0 py-3">
                                <h5 class="mb-0"><i class="bi bi-list-ul me-2"></i>All Clubs</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${clubs != null && !clubs.isEmpty()}">
                                        <div class="table-responsive">
                                            <table class="table table-hover">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>CLUB NAME</th>
                                                        <th>DESCRIPTION</th>
                                                        <th>MEETING SCHEDULE</th>
                                                        <th>CREATED BY</th>
                                                        <th>ACTIONS</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="club" items="${clubs}">
                                                        <tr>
                                                            <td>
                                                                <div class="d-flex align-items-center">
                                                                    <div class="bg-primary bg-opacity-10 rounded-circle p-2 me-3">
                                                                        <i class="bi bi-people-fill text-primary"></i>
                                                                    </div>
                                                                    <div>
                                                                        <h6 class="mb-0">${club.name}</h6>
                                                                        <small class="text-muted">ID: ${club.id}</small>
                                                                    </div>
                                                                </div>
                                                            </td>
                                                            <td>
                                                                <p class="mb-0 text-truncate" style="max-width: 200px;">
                                                                    ${club.description != null ? club.description : 'No description available'}
                                                                </p>
                                                            </td>
                                                            <td>
                                                                <span class="badge bg-info">${club.meetingScheduleTitle != null ? club.meetingScheduleTitle : 'No schedule set'}</span>
                                                            </td>
                                                            <td>
                                                                <small class="text-muted">${club.createdBy != null ? club.createdBy.firstName : 'Unknown'}</small>
                                                            </td>
                                                            <td>
                                                                <div class="btn-group" role="group">
                                                                    <a href="${pageContext.request.contextPath}/admin/clubs/edit/${club.id}" 
                                                                       class="btn btn-sm btn-outline-primary" title="Edit">
                                                                        <i class="bi bi-pencil"></i>
                                                                    </a>
                                                                    <button class="btn btn-sm btn-outline-danger" title="Delete" 
                                                                            onclick="confirmDelete(${club.id}, '${club.name}')"
                                                                            data-club-name="${club.name}">
                                                                        <i class="bi bi-trash"></i>
                                                                    </button>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="bi bi-people text-muted" style="font-size: 3rem;"></i>
                                            <h5 class="text-muted mt-3">No clubs found</h5>
                                            <p class="text-muted">Create your first club to get started!</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Add Club Modal -->
    <div class="modal fade" id="addClubModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Add New Club</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="${pageContext.request.contextPath}/admin/clubs/create" method="post" id="createClubForm">
                        <div class="mb-3">
                            <label for="clubName" class="form-label">Club Name</label>
                            <input type="text" class="form-control" id="clubName" name="name" placeholder="Enter club name" required>
                        </div>
                        <div class="mb-3">
                            <label for="clubDescription" class="form-label">Description</label>
                            <textarea class="form-control" id="clubDescription" name="description" rows="3" placeholder="Enter club description"></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="clubMeetingSchedule" class="form-label">Meeting Schedule Title</label>
                            <input type="text" class="form-control" id="clubMeetingSchedule" name="meetingScheduleTitle" placeholder="e.g., Every Monday 2:00 PM, Weekly Friday 3:30 PM">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" form="createClubForm" class="btn btn-primary">Create Club</button>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../layout/footer.jsp" />
    
    <!-- Bootstrap JS Bundle with Popper -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- Success/Error Message Display -->
    <script>
      // Check for flash messages
        <c:if test="${not empty success}">
            // Show success message
            document.addEventListener('DOMContentLoaded', function() {
                const successAlert = document.createElement('div');
                successAlert.className = 'alert alert-success alert-dismissible fade show position-fixed';
                successAlert.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
                successAlert.innerHTML = `
                    <i class="bi bi-check-circle me-2"></i>${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                document.body.appendChild(successAlert);
                
                // Auto remove after 5 seconds
                setTimeout(() => {
                    if (successAlert.parentNode) {
                        successAlert.remove();
                    }
                }, 5000);
            });
        </c:if>
        
        <c:if test="${not empty error}">
            // Show error message
            document.addEventListener('DOMContentLoaded', function() {
                const errorAlert = document.createElement('div');
                errorAlert.className = 'alert alert-danger alert-dismissible fade show position-fixed';
                errorAlert.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
                errorAlert.innerHTML = `
                    <i class="bi bi-exclamation-triangle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                document.body.appendChild(errorAlert);
                
                // Auto remove after 5 seconds
                setTimeout(() => {
                    if (errorAlert.parentNode) {
                        errorAlert.remove();
                    }
                }, 5000);
            });
        </c:if>
        
        // Form validation
        document.getElementById('createClubForm').addEventListener('submit', function(e) {
            const clubName = document.getElementById('clubName').value.trim();
            if (clubName === '') {
                e.preventDefault();
                alert('Please enter a club name');
                return false;
            }
        });
        
        // Clear form when modal is closed
        document.getElementById('addClubModal').addEventListener('hidden.bs.modal', function() {
            document.getElementById('createClubForm').reset();
        });
        
        // Delete confirmation function
        function confirmDelete(clubId, clubName) {
            console.log('confirmDelete called with clubId:', clubId, 'clubName:', clubName);
            
            if (confirm('Are you sure you want to delete the club "' + clubName + '"? This action cannot be undone.')) {
                console.log('User confirmed deletion');
                
                // Create and submit delete form
                var form = document.createElement('form');
                form.method = 'POST';
                form.action = '${pageContext.request.contextPath}/admin/clubs/delete/' + clubId;
                
                console.log('Form action:', form.action);
                
                // Add CSRF token if needed
                var csrfToken = document.querySelector('meta[name="_csrf"]');
                if (csrfToken) {
                    var csrfInput = document.createElement('input');
                    csrfInput.type = 'hidden';
                    csrfInput.name = '_csrf';
                    csrfInput.value = csrfToken.getAttribute('content');
                    form.appendChild(csrfInput);
                    console.log('CSRF token added');
                } else {
                    console.log('No CSRF token found');
                }
                
                document.body.appendChild(form);
                console.log('Form submitted');
                form.submit();
            } else {
                console.log('User cancelled deletion');
            }
        }
        
        // Search functionality for clubs
        document.addEventListener('DOMContentLoaded', function() {
            const searchInput = document.getElementById('clubSearchInput');
            const tableBody = document.querySelector('tbody');
            const tableRows = tableBody.querySelectorAll('tr');
            
            searchInput.addEventListener('input', function() {
                const searchTerm = this.value.toLowerCase().trim();
                
                tableRows.forEach(row => {
                    const clubName = row.querySelector('h6')?.textContent?.toLowerCase() || '';
                    const description = row.querySelector('p')?.textContent?.toLowerCase() || '';
                    const meetingSchedule = row.querySelector('.badge')?.textContent?.toLowerCase() || '';
                    const createdBy = row.querySelector('small')?.textContent?.toLowerCase() || '';
                    
                    const matchesSearch = clubName.includes(searchTerm) || 
                                        description.includes(searchTerm) || 
                                        meetingSchedule.includes(searchTerm) || 
                                        createdBy.includes(searchTerm);
                    
                    row.style.display = matchesSearch ? '' : 'none';
                });
                
                // Show "no results" message if no matches
                const visibleRows = Array.from(tableRows).filter(row => row.style.display !== 'none');
                const noResultsRow = tableBody.querySelector('.no-results-row');
                
                if (visibleRows.length === 0 && !noResultsRow) {
                    const noResults = document.createElement('tr');
                    noResults.className = 'no-results-row';
                    noResults.innerHTML = `
                        <td colspan="5" class="text-center text-muted py-4">
                            <i class="bi bi-search text-muted me-2"></i>
                            No clubs found matching "${searchTerm}"
                        </td>
                    `;
                    tableBody.appendChild(noResults);
                } else if (visibleRows.length > 0 && noResultsRow) {
                    noResultsRow.remove();
                }
            });
            
            // Clear search when input is cleared
            searchInput.addEventListener('change', function() {
                if (this.value === '') {
                    tableRows.forEach(row => {
                        row.style.display = '';
                    });
                    const noResultsRow = tableBody.querySelector('.no-results-row');
                    if (noResultsRow) {
                        noResultsRow.remove();
                    }
                }
            });
        });
    </script>
</body>
</html>
