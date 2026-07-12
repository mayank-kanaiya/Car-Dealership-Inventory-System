import PropTypes from 'prop-types';
import { useState } from 'react';
import { Search, X } from 'lucide-react';
import Button from '../../../components/Button/Button';
import { VEHICLE_CATEGORIES } from '../../../constants/vehicleCategories';

function VehicleFilters({ onFilter, initialFilters = {} }) {
  const [make, setMake] = useState(initialFilters.make || '');
  const [category, setCategory] = useState(initialFilters.category || '');

  const handleSearch = () => {
    onFilter({ make: make.trim(), category });
  };

  const handleClear = () => {
    setMake('');
    setCategory('');
    onFilter({ make: '', category: '' });
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') handleSearch();
  };

  return (
    <div className="flex flex-col sm:flex-row gap-3 items-end">
      <div className="flex-1 w-full relative">
        <label
          htmlFor="vehicle-search"
          className="block text-sm font-medium text-text-secondary mb-1.5"
        >
          Search
        </label>
        <div className="relative">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-text-muted" />
          <input
            id="vehicle-search"
            type="text"
            value={make}
            onChange={(e) => setMake(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Search by make..."
            className="w-full pl-10 pr-3 py-2.5 text-sm border border-border rounded-xl bg-surface text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
          />
          {make && (
            <button
              onClick={() => {
                setMake('');
                onFilter({ make: '', category });
              }}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-text-muted hover:text-text-primary cursor-pointer"
              aria-label="Clear search"
            >
              <X size={14} />
            </button>
          )}
        </div>
      </div>
      <div className="w-full sm:w-48">
        <label
          htmlFor="category-filter"
          className="block text-sm font-medium text-text-secondary mb-1.5"
        >
          Category
        </label>
        <select
          id="category-filter"
          value={category}
          onChange={(e) => {
            setCategory(e.target.value);
            onFilter({ make: make.trim(), category: e.target.value });
          }}
          className="w-full px-3 py-2.5 text-sm border border-border rounded-xl bg-surface text-text-primary focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
        >
          <option value="">All Categories</option>
          {VEHICLE_CATEGORIES.map((cat) => (
            <option key={cat.value} value={cat.value}>
              {cat.label}
            </option>
          ))}
        </select>
      </div>
      <div className="flex gap-2">
        <Button variant="primary" size="md" onClick={handleSearch}>
          Search
        </Button>
        {(make || category) && (
          <Button variant="ghost" size="md" onClick={handleClear}>
            Clear
          </Button>
        )}
      </div>
    </div>
  );
}

VehicleFilters.propTypes = {
  onFilter: PropTypes.func.isRequired,
  initialFilters: PropTypes.shape({
    make: PropTypes.string,
    category: PropTypes.string,
  }),
};

export default VehicleFilters;
