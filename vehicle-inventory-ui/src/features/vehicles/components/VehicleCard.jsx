import PropTypes from 'prop-types';
import { useNavigate } from 'react-router-dom';
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
  const categoryLabel =
    VEHICLE_CATEGORIES.find((c) => c.value === vehicle.category)?.label || vehicle.category;

  return (
    <div
      className="bg-surface rounded-2xl border border-border overflow-hidden hover:shadow-xl hover:shadow-primary/5 hover:-translate-y-1 transition-all duration-300 cursor-pointer group"
      onClick={() => navigate(`/vehicles/${vehicle.id}`)}
      role="article"
      aria-label={`${vehicle.make} ${vehicle.model}`}
    >
      <div className="aspect-video bg-gradient-to-br from-surface-secondary to-gray-200 dark:to-gray-800 overflow-hidden relative">
        <img
          src={vehicle.imageUrl || '/images/default-vehicle.svg'}
          alt={`${vehicle.make} ${vehicle.model}`}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
          onError={(e) => {
            e.target.style.display = 'none';
            e.target.parentElement.classList.add('flex', 'items-center', 'justify-center');
            const placeholder = document.createElement('div');
            placeholder.className = 'flex flex-col items-center text-text-muted';
            placeholder.innerHTML =
              '<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M19 17h2c.6 0 1-.4 1-1v-3c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4c0 .6.4 1 1 1h2"/><circle cx="7" cy="17" r="2"/><path d="M9 17h6"/><circle cx="17" cy="17" r="2"/></svg>';
            e.target.parentElement.appendChild(placeholder);
          }}
        />
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
