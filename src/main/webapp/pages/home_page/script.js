document.addEventListener('DOMContentLoaded', function() {
	
	// Initialize tooltips
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
		return new bootstrap.Tooltip(tooltipTriggerEl);
	});

});

// AI-Enhanced Search
async function enhanceSearch() {
    const searchInput = document.getElementById('searchInput');
    const query = searchInput.value.trim();
    
    if (!query) {
        alert('Vui lòng nhập từ khóa tìm kiếm!');
        return;
    }
    
    searchInput.disabled = true;
    const originalPlaceholder = searchInput.placeholder;
    searchInput.placeholder = 'Đang xử lý với AI...';
    
    try {
        const contextPath = window.location.pathname.split('/')[1];
        const response = await fetch('/' + contextPath + '/AISearchController', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'query=' + encodeURIComponent(query) + '&context=binhluan'
        });
        
        const data = await response.json();
        
        if (data.success) {
            searchInput.value = data.enhancedQuery;
            const form = searchInput.closest('form');
            if (form) {
                setTimeout(() => form.submit(), 100);
            }
        } else {
            alert('Không thể xử lý với AI. Tìm kiếm thường...');
            searchInput.closest('form').submit();
        }
    } catch (error) {
        console.error('AI Search error:', error);
        searchInput.closest('form').submit();
    } finally {
        searchInput.disabled = false;
        searchInput.placeholder = originalPlaceholder;
    }
}

// Handle sort selection
function handleSort(value) {
	if(!value) return;
	
	const urlParams = new URLSearchParams(window.location.search);
	const contextPath = window.location.pathname.split('/')[1];
	const theloai = urlParams.get('theloai');
	const search = urlParams.get('search');
	const filterTK = urlParams.get('filterTK');
	const page = urlParams.get('page');
	
	let url = '/' + contextPath + '/TrangChuController?sort=' + value;
	if(theloai) url += '&theloai=' + encodeURIComponent(theloai);
	if(search) url += '&search=' + encodeURIComponent(search);
	if(filterTK) url += '&filterTK=' + encodeURIComponent(filterTK);
	if(page) url += '&page=' + encodeURIComponent(page);
	
	window.location.href = url;
}

// Handle filter by tai khoan
function handleFilterTaiKhoan(value) {
	const urlParams = new URLSearchParams(window.location.search);
	const contextPath = window.location.pathname.split('/')[1];
	const theloai = urlParams.get('theloai');
	const search = urlParams.get('search');
	const sort = urlParams.get('sort');
	const page = urlParams.get('page');
	
	let url = '/' + contextPath + '/TrangChuController';
	let params = [];
	
	if(theloai) params.push('theloai=' + encodeURIComponent(theloai));
	if(search) params.push('search=' + encodeURIComponent(search));
	if(sort) params.push('sort=' + sort);
	if(value) params.push('filterTK=' + encodeURIComponent(value));
	if(page) params.push('page=' + encodeURIComponent(page));
	
	if(params.length > 0) url += '?' + params.join('&');
	
	window.location.href = url;
}

// Handle real-time search
/*
let searchTimeout;
function handleSearch() {
	const searchInput = document.getElementById('searchInput');
	const searchValue = searchInput.value.trim();
	
	// Clear previous timeout
	clearTimeout(searchTimeout);
	
	// Set new timeout for search (debounce)
	searchTimeout = setTimeout(() => {
		const urlParams = new URLSearchParams(window.location.search);
		const contextPath = window.location.pathname.split('/')[1];
		const theloai = urlParams.get('theloai');
		const sort = urlParams.get('sort');
		const filterTK = urlParams.get('filterTK');
		const page = urlParams.get('page');
		
		let url = '/' + contextPath + '/TrangChuController';
		let params = [];
		
		if(theloai) params.push('theloai=' + encodeURIComponent(theloai));
		if(sort) params.push('sort=' + encodeURIComponent(sort));
		if(filterTK) params.push('filterTK=' + encodeURIComponent(filterTK));
		if(page) params.push('page=' + encodeURIComponent(page));
		if(searchValue) params.push('search=' + encodeURIComponent(searchValue));
		
		if(params.length > 0) url += '?' + params.join('&');
		
		window.location.href = url;
	}, 300); // Wait 300ms after user stops typing
}
*/

// Close modal on escape key
document.addEventListener('keydown', function(e) {
	if(e.key === 'Escape') {
		const modal = bootstrap.Modal.getInstance(document.getElementById('postDetailModal'));
		if(modal) modal.hide();
	}
});