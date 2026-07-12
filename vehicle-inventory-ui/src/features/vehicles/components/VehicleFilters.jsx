import PropTypes from 'prop-types';
import { useState } from 'react';
import SearchBar from '../../../components/SearchBar/SearchBar';
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
      <div className="flex-1 w-full">
        <label
          htmlFor="vehicle-search"
          className="block text-sm font-medium text-text-secondary mb-1"
        >
          Search
        </label>
        <SearchBar
          value={make}
          onChange={setMake}
          placeholder="Search by make..."
          onKeyDown={handleKeyDown}
        />
      </div>
      <div className="w-full sm:w-48">
        <label
          htmlFor="category-filter"
          className="block text-sm font-medium text-text-secondary mb-1"
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
          className="w-full px-3 py-2 text-sm border border-border rounded-lg bg-surface text-text-primary focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-colors"
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
        <Button variant="primary" size="sm" onClick={handleSearch}>
          Search
        </Button>
        <Button variant="ghost" size="sm" onClick={handleClear}>
          Clear
        </Button>
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
