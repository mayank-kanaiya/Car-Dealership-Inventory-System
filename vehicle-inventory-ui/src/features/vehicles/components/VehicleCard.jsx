import { useState } from 'react';
import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
import { ImageOff } from 'lucide-react';
import Badge from '../../../components/Badge/Badge';
import { VEHICLE_CATEGORIES } from '../../../constants/vehicleCategories';

const categoryBadgeVariant = {
  SEDAN: 'info',
  SUV: 'success',
  HATCHBACK: 'warning',
  PICKUP_TRUCK: 'default',
  ELECTRIC_SUV: 'success',
  SPORTS_CAR: 'danger',
  MINIVAN: 'default',
  CROSSOVER: 'info',
  OFF_ROAD: 'warning',
  MOTORCYCLE: 'danger',
};

function VehicleCard({ vehicle }) {
  const navigate = useNavigate();
  const [imgError, setImgError] = useState(false);
  const categoryLabel =
    VEHICLE_CATEGORIES.find((c) => c.value === vehicle.category)?.label || vehicle.category;

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      navigate(`/vehicles/${vehicle.id}`);
    }
  };

  return (
    <div
      className="bg-surface rounded-2xl border border-border overflow-hidden hover:shadow-xl hover:shadow-primary/5 hover:-translate-y-1 transition-all duration-300 cursor-pointer group"
      onClick={() => navigate(`/vehicles/${vehicle.id}`)}
      onKeyDown={handleKeyDown}
      tabIndex={0}
      role="link"
      aria-label={`${vehicle.make} ${vehicle.model}`}
    >
      <div className="aspect-video bg-gradient-to-br from-surface-secondary to-gray-200 dark:to-gray-800 overflow-hidden relative">
        {imgError ? (
          <div className="w-full h-full flex flex-col items-center justify-center gap-2 text-text-muted">
            <ImageOff size={40} strokeWidth={1.5} />
            <span className="text-xs">No image</span>
          </div>
        ) : (
          <img
            src={vehicle.imageUrl || '/images/default-vehicle.svg'}
            alt={`${vehicle.make} ${vehicle.model}`}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
            onError={() => setImgError(true)}
          />
        )}
        <div className="absolute top-3 right-3">
          <Badge variant={categoryBadgeVariant[vehicle.category] || 'default'}>
            {categoryLabel}
          </Badge>
        </div>
      </div>
      <div className="p-4 sm:p-5">
        <h3 className="text-base font-semibold text-text-primary truncate mb-1 group-hover:text-primary transition-colors">
          {vehicle.make} {vehicle.model}
        </h3>
        <div className="flex items-baseline justify-between mt-3">
          <span className="text-xl font-bold text-primary">
            ${vehicle.price.toLocaleString('en-US', { minimumFractionDigits: 2 })}
          </span>
          <span
            className={`text-xs font-medium px-2 py-0.5 rounded-full ${
              vehicle.quantityInStock > 0
                ? 'bg-green-50 text-success dark:bg-green-950/50'
                : 'bg-red-50 text-danger dark:bg-red-950/50'
            }`}
          >
            {vehicle.quantityInStock > 0 ? `${vehicle.quantityInStock} in stock` : 'Out of stock'}
          </span>
        </div>
      </div>
    </div>
  );
}

VehicleCard.propTypes = {
  vehicle: PropTypes.shape({
    id: PropTypes.string.isRequired,
    make: PropTypes.string.isRequired,
    model: PropTypes.string.isRequired,
    category: PropTypes.string.isRequired,
    price: PropTypes.number.isRequired,
    quantityInStock: PropTypes.number.isRequired,
    imageUrl: PropTypes.string,
  }).isRequired,
};

export default VehicleCard;
