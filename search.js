document.addEventListener('DOMContentLoaded', function() {
    let currentPage = 1;
    const pageSize = 10; // Define pageSize to match backend default

    // form submission
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        currentPage = 1; // Reset to first page on new search
        performSearch();
    });

    //  previous page button
    document.getElementById('prevPage').addEventListener('click', function() {
        if (currentPage > 1) {
            currentPage--;
            performSearch();
        }
    });

    // next page button
    document.getElementById('nextPage').addEventListener('click', function() {
        currentPage++;
        performSearch();
    });

   
    const filters = ['documentType', 'fileType', 'year'];
    filters.forEach(filterId => {
        document.getElementById(filterId).addEventListener('change', function() {
            currentPage = 1; 
            performSearch();
        });
    });

    function performSearch() {
        const keyword = document.getElementById('keyword').value.trim();
        const documentType = document.getElementById('documentType').value;
        const fileType = document.getElementById('fileType').value;
        const year = document.getElementById('year').value;

        if (!keyword) {
            alert("Please enter a search term.");
            return;
        }

        const params = new URLSearchParams({
            keyword,
            documentType: documentType === "All documents" ? "" : documentType,
            fileType: fileType === "All types" ? "" : fileType,
            year: year === "All years" ? "" : year,
            page: currentPage - 1,
            size: pageSize
        });

        fetch(`http://localhost:8080/api/document/search?${params.toString()}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Cache-Control': 'no-cache'
            }
        })
            .then(resp => {
                if (!resp.ok) throw new Error(`HTTP error: ${resp.status}`);
                return resp.json(); 
            })
            .then(data => {
                renderResults(data.content); 
                updatePagination(data.total, data.size, data.page);
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('cardContainer').innerHTML = `<p>Error fetching results: ${error.message}</p>`;
            });
    }

    function renderResults(results) {
        const container = document.getElementById('cardContainer');
        container.innerHTML = ''; // Clear existing cards
        if (!results || results.length === 0) {
            container.innerHTML = '<p>No results found.</p>';
            return;
        }

        // Create result cards
        results.forEach(result => {
            const card = document.createElement('div');
            card.className = 'card';
            card.innerHTML = `
                <h3>${result.title || 'Untitled'}</h3>
                <p>Author: ${result.author || 'Unknown'}</p>
                <p>Uploaded: ${result.dateCreation || 'Unknown'}</p>
                <button class="btn-download" onclick="downloadFile('${result.id}')">
                    <i class='bx bxs-download'></i>
                </button>
            `;
            container.appendChild(card);
        });
    }

    function updatePagination(totalHits, pageSize, currentPage) {
        const totalPages = Math.ceil(totalHits / pageSize);
        const pageInfo = document.getElementById('pageInfo');
        const prevButton = document.getElementById('prevPage');
        const nextButton = document.getElementById('nextPage');

        pageInfo.textContent = `Page ${currentPage + 1}/${totalPages || 1}`;
        prevButton.disabled = currentPage === 0;
        nextButton.disabled = currentPage + 1 >= totalPages;
    }


    function downloadFile(id) {

        window.location.href = `/api/document/open-file/${encodeURIComponent(id)}`;
    }


});