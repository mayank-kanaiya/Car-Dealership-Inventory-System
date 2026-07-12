import PropTypes from 'prop-types';
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from 'lucide-react';

function getVisiblePages(currentPage, totalPages) {
  const delta = 2;
  const pages = [];
  const left = Math.max(2, currentPage - delta);
  const right = Math.min(totalPages - 1, currentPage + delta);

  pages.push(1);
  if (left > 2) pages.push('...');
  for (let i = left; i <= right; i++) pages.push(i);
  if (right < totalPages - 1) pages.push('...');
  if (totalPages > 1) pages.push(totalPages);

  return pages;
}

function Pagination({ currentPage, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const pages = getVisiblePages(currentPage, totalPages);

  const btnClass = (active) =>
    `min-w-[36px] h-9 flex items-center justify-center text-sm rounded-lg transition-all duration-150 cursor-pointer ${
      active
        ? 'bg-primary text-white font-medium shadow-sm shadow-primary/25'
        : 'text-text-secondary hover:bg-surface-hover hover:text-text-primary'
    }`;

  return (
    <nav aria-label="Pagination" className="flex items-center justify-center gap-1">
      <button
        type="button"
        onClick={() => onPageChange(1)}
        disabled={currentPage === 1}
        aria-label="First page"
        className="p-2 rounded-lg text-text-secondary hover:bg-surface-hover hover:text-text-primary disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer transition-colors"
      >
        <ChevronsLeft size={16} />
      </button>
      <button
        type="button"
        onClick={() => onPageChange(currentPage - 1)}
        disabled={currentPage === 1}
        aria-label="Previous"
        className="p-2 rounded-lg text-text-secondary hover:bg-surface-hover hover:text-text-primary disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer transition-colors"
      >
        <ChevronLeft size={16} />
      </button>
      {pages.map((page, idx) =>
        page === '...' ? (
          <span key={`ellipsis-${idx}`} className="px-1 text-text-muted text-sm">
            ...
          </span>
        ) : (
          <button
            key={page}
            type="button"
            onClick={() => onPageChange(page)}
            aria-current={page === currentPage ? 'page' : undefined}
            className={btnClass(page === currentPage)}
          >
            {page}
          </button>
        )
      )}
      <button
        type="button"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
        aria-label="Next"
        className="p-2 rounded-lg text-text-secondary hover:bg-surface-hover hover:text-text-primary disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer transition-colors"
      >
        <ChevronRight size={16} />
      </button>
      <button
        type="button"
        onClick={() => onPageChange(totalPages)}
        disabled={currentPage === totalPages}
        aria-label="Last page"
        className="p-2 rounded-lg text-text-secondary hover:bg-surface-hover hover:text-text-primary disabled:opacity-30 disabled:cursor-not-allowed cursor-pointer transition-colors"
      >
        <ChevronsRight size={16} />
      </button>
    </nav>
  );
}

Pagination.propTypes = {
  currentPage: PropTypes.number.isRequired,
  totalPages: PropTypes.number.isRequired,
  onPageChange: PropTypes.func.isRequired,
};

export default Pagination;
