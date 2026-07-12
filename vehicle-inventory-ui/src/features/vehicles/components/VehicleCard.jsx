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
      className="bg-surface rounded-xl border border-border overflow-hidden hover:shadow-lg transition-shadow duration-200 cursor-pointer"
      onClick={() => navigate(`/vehicles/${vehicle.id}`)}
      role="article"
      aria-label={`${vehicle.make} ${vehicle.model}`}
    >
      <div className="aspect-video bg-gray-100 overflow-hidden">
        <img
          src={vehicle.imageUrl || '/images/default-vehicle.svg'}
          alt={`${vehicle.make} ${vehicle.model}`}
          className="w-full h-full object-cover"
          onError={(e) => {
            e.target.src = '/images/default-vehicle.svg';
          }}
        />
      </div>
      <div className="p-4">
        <div className="flex items-start justify-between gap-2 mb-2">
          <h3 className="text-lg font-semibold text-text-primary truncate">
            {vehicle.make} {vehicle.model}
          </h3>
          <Badge variant={categoryBadgeVariant[vehicle.category] || 'default'}>
            {categoryLabel}
          </Badge>
        </div>
        <div className="flex items-baseline justify-between">
          <span className="text-xl font-bold text-primary">
            ${vehicle.price.toLocaleString('en-US', { minimumFractionDigits: 2 })}
          </span>
          <span
            className={`text-sm font-medium ${vehicle.quantityInStock > 0 ? 'text-success' : 'text-danger'}`}
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
